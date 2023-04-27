package com.aqualen.subscriptionchanneltelegrambot;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SubscriptionChannelTelegramBotApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(SubscriptionChannelTelegramBotApplication.class, args);
    }

}
