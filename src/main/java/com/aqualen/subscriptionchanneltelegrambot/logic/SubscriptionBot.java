package com.aqualen.subscriptionchanneltelegrambot.logic;

import com.aqualen.subscriptionchanneltelegrambot.entity.Channel;
import com.aqualen.subscriptionchanneltelegrambot.entity.User;
import com.aqualen.subscriptionchanneltelegrambot.enums.PaymentTypes;
import com.aqualen.subscriptionchanneltelegrambot.enums.Periods;
import com.aqualen.subscriptionchanneltelegrambot.props.BotProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.ChatInviteLink;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.aqualen.subscriptionchanneltelegrambot.enums.Periods.subscriptionPeriod;
import static com.aqualen.subscriptionchanneltelegrambot.logic.ChannelService.CHOOSE_CHANNEL_MESSAGE;
import static com.aqualen.subscriptionchanneltelegrambot.logic.PaymentService.CHOOSE_PAYMENT_MESSAGE;
import static com.aqualen.subscriptionchanneltelegrambot.util.BotUtils.generateButtons;

@Slf4j
@Component
public class SubscriptionBot extends TelegramLongPollingBot {

    public static final String USER_NOT_FOUND_EXC = "User with username %s was not found!";
    private final BotProperties botProperties;
    private final ChannelService channelService;
    private final PaymentService paymentService;
    Map<String, User> usersCache = new HashMap<>();

    public SubscriptionBot(@Value("${bot.token}") String botToken,
                           BotProperties botProperties,
                           ChannelService channelService,
                           PaymentService paymentService) {
        super(botToken);
        this.botProperties = botProperties;
        this.channelService = channelService;
        this.paymentService = paymentService;
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            initiateMessageAction(update);
        }

        if (update.hasCallbackQuery()) {
            initiateCallbackAction(update);
        }

        if (update.hasPreCheckoutQuery()) {
            execute(AnswerPreCheckoutQuery.builder()
                    .preCheckoutQueryId(update.getPreCheckoutQuery().getId())
                    .ok(true).build());
        }
    }

    @SneakyThrows
    private void initiateMessageAction(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId();
        var username = message.getChat().getUserName();

        if (message.hasText() && message.getText().equals("/start")) {
            usersCache.put(username, User.builder()
                    .username(username)
                    .userId(message.getFrom().getId()).build());
            sendButtons(chatId, CHOOSE_CHANNEL_MESSAGE, channelService.getChannelNames());
        }
        if (Objects.nonNull(message.getSuccessfulPayment()) &&
                !message.getSuccessfulPayment().getProviderPaymentChargeId().isEmpty()) {
            ChatInviteLink chatInviteLink = execute(channelService.getChatInviteLink(
                    usersCache.get(username).getChannelId())
            );
            execute(SendMessage.builder().text(chatInviteLink.getInviteLink()).build());
        }
    }

    private void initiateCallbackAction(Update update) {
        var username = update.getCallbackQuery().getFrom().getUserName();
        var message = update.getCallbackQuery().getMessage();
        var chatId = message.getChatId();
        var data = update.getCallbackQuery().getData();
        try {
            User user = usersCache.get(username);
            if (Objects.nonNull(user)) {
                if (data.contains(Channel.CHANNEL_PREFIX)) {
                    user.setChannelId(data);
                    sendButtons(chatId, Periods.CHOOSE_PERIOD_MESSAGE, subscriptionPeriod);
                }

                if (EnumUtils.isValidEnum(Periods.class, data)) {
                    user.setPeriods(Periods.valueOf(data));
                    sendButtons(chatId, CHOOSE_PAYMENT_MESSAGE, paymentService.getPaymentTypes());
                }

                if (EnumUtils.isValidEnum(PaymentTypes.class, data)) {
                    user.setPaymentType(PaymentTypes.valueOf(data));
                    execute(paymentService.generateInvoice(chatId, user));
                }

                usersCache.put(username, user);
            } else {
                throw new TelegramApiException(String.format(USER_NOT_FOUND_EXC, username));
            }
        } catch (TelegramApiException e) {
            log.warn(e.getMessage(), e);
            sendButtons(chatId, CHOOSE_CHANNEL_MESSAGE, channelService.getChannelNames());
        }
    }

    @SneakyThrows
    private void sendButtons(Long chatId, String text, Map<?, String> buttonsList) {
        InlineKeyboardMarkup buttons = new InlineKeyboardMarkup();
        buttons.setKeyboard(generateButtons(buttonsList));
        var response = new SendMessage(String.valueOf(chatId), text);
        response.setReplyMarkup(buttons);
        execute(response);
    }
}
