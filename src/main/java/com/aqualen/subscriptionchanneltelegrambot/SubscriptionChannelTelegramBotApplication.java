package com.aqualen.subscriptionchanneltelegrambot;

import com.aqualen.subscriptionchanneltelegrambot.entity.PaymentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class SubscriptionChannelTelegramBotApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(SubscriptionChannelTelegramBotApplication.class, args);
    }

}
