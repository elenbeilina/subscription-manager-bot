package com.aqualen.subscriptionchanneltelegrambot.logic;

import com.aqualen.subscriptionchanneltelegrambot.entity.Period;
import com.aqualen.subscriptionchanneltelegrambot.props.BotProperties;
import lombok.SneakyThrows;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


@Component
public class SubscriptionBot extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    private final Map<String, String> channelNames = Map.of("channel_test1", "Канал 1");
    private final Map<Period, String> subscriptionPeriod = new TreeMap<>(Map.of(
            Period.WEEK, "Неделя",
            Period.MONTH, "Месяц",
            Period.FOREVER, "Навсегда"));
    private final Map<String, String> paymentType = Map.of(
            "payment_yoomoney", "Юмани"
    );

    public SubscriptionBot(BotProperties botProperties,
                           @Value("${bot.token}") String botToken) {
        super(botToken);
        this.botProperties = botProperties;
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            var msg = update.getMessage();
            var chatId = msg.getChatId();

            if (msg.getText().equals("/start")) {
                sendButtons(chatId, "Выберите канал:", channelNames);
            }
        }

        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            var chatId = update.getCallbackQuery().getMessage().getChatId();

            if (data.contains("channel")) {
                sendButtons(chatId, "Выберите период подписки:", subscriptionPeriod);
            }

            if (EnumUtils.isValidEnum(Period.class, data)) {
                sendButtons(chatId, "Выберите тип оплаты:", paymentType);
            }

            if (data.contains("payment")) {
                execute(new SendMessage(String.valueOf(chatId), "Оплата недоступна."));
            }
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
