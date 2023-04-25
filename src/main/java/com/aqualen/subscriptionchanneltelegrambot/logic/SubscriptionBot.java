package com.aqualen.subscriptionchanneltelegrambot.logic;

import com.aqualen.subscriptionchanneltelegrambot.entity.Period;
import com.aqualen.subscriptionchanneltelegrambot.entity.User;
import com.aqualen.subscriptionchanneltelegrambot.props.BotProperties;
import com.aqualen.subscriptionchanneltelegrambot.props.PaymentProperties;
import lombok.SneakyThrows;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;


@Component
public class SubscriptionBot extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    private final PaymentProperties paymentProperties;
    private final Map<String, String> channelNames = Map.of("channel_test1", "Канал 1");
    private final Map<Period, String> subscriptionPeriod = new TreeMap<>(Map.of(
            Period.WEEK, Period.WEEK.getName(),
            Period.MONTH, Period.MONTH.getName(),
            Period.FOREVER, Period.FOREVER.getName()));
    private final Map<String, String> paymentType = Map.of(
            "payment_yoomoney", "Юмани"
    );
    Map<String, User> usersCache = new HashMap<>();

    public SubscriptionBot(BotProperties botProperties,
                           @Value("${bot.token}") String botToken,
                           PaymentProperties paymentProperties) {
        super(botToken);
        this.botProperties = botProperties;
        this.paymentProperties = paymentProperties;
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            var message = update.getMessage();
            var chatId = message.getChatId();

            if (message.hasText() && message.getText().equals("/start")) {
                sendButtons(chatId, "Выберите канал:", channelNames);
            }
            if(Objects.nonNull(message.getSuccessfulPayment()) &&
                    !message.getSuccessfulPayment().getProviderPaymentChargeId().isEmpty()){
                execute(new SendMessage(String.valueOf(chatId), "Добавляю в канал!"));
            }
        }

        if (update.hasCallbackQuery()) {
            String username = update.getCallbackQuery().getFrom().getUserName();
            String data = update.getCallbackQuery().getData();
            var chatId = update.getCallbackQuery().getMessage().getChatId();

            if (data.contains("channel")) {
                usersCache.put(username, User.builder()
                        .username(username)
                        .channelName(data)
                        .build());
                sendButtons(chatId, "Выберите период подписки:", subscriptionPeriod);
            }

            if (EnumUtils.isValidEnum(Period.class, data)) {
                User user = usersCache.get(username);
                if (Objects.nonNull(user)) {
                    user.setPeriod(Period.valueOf(data));
                    usersCache.put(username, user);
                    sendButtons(chatId, "Выберите тип оплаты:", paymentType);
                } else {
                    sendButtons(chatId, "Выберите канал:", channelNames);
                }
            }

            if (data.contains("payment")) {
                User user = usersCache.get(username);
                if (Objects.nonNull(user)) {
                    execute(SendInvoice.builder()
                            .chatId(chatId)
                            .title("Оплата подписки.")
                            .description(String.format("Канал: %s на период: %s.",
                                    channelNames.get(user.getChannelName()),
                                    user.getPeriod().getName()))
                            .currency("RUB")
                            .startParameter("test")
                            .providerToken(paymentProperties.getYoomoneyToken())
                            .payload(String.format("Оплата подписки на канал: %s на период: %s юзером: %s.",
                                    channelNames.get(user.getChannelName()),
                                    user.getPeriod().getName(), user.getUsername()))
                            .price(LabeledPrice.builder().label(user.getPeriod().getName()).amount(100 * 100).build()
                            ).build());
                } else {
                    sendButtons(chatId, "Выберите канал:", channelNames);
                }
            }
        }

        if (update.hasPreCheckoutQuery()){
            execute(AnswerPreCheckoutQuery.builder()
                    .preCheckoutQueryId(update.getPreCheckoutQuery().getId())
                    .ok(true).build());
        }
    }

    private List<List<InlineKeyboardButton>> generateButtons(Map<?, String> buttons) {
        List<List<InlineKeyboardButton>> rowsOfButtons = new ArrayList<>();
        for (Map.Entry<?, String> button : buttons.entrySet()) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            rowInline.add(
                    InlineKeyboardButton.builder()
                            .text(button.getValue())
                            .callbackData(button.getKey().toString())
                            .pay(true).build()
            );

            rowsOfButtons.add(rowInline);
        }
        return rowsOfButtons;
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
