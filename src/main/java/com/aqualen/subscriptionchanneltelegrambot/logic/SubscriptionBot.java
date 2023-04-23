package com.aqualen.subscriptionchanneltelegrambot.logic;

import com.aqualen.subscriptionchanneltelegrambot.props.BotProperties;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
public class SubscriptionBot extends TelegramLongPollingBot {

    private final BotProperties botProperties;

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
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            var msg = update.getMessage();
            var chatId = String.valueOf(msg.getChatId());

            if (msg.getText().equals("/start")) {
                sendNotification(chatId, "Enter your name:");
            } else {
                var reply = "Hey yo, " + msg.getText() + "!";
                sendNotification(chatId, reply);
            }
        }
    }

    @SneakyThrows
    private void sendNotification(String chatId, String msg) {
        var response = new SendMessage(chatId, msg);
        execute(response);
    }
}
