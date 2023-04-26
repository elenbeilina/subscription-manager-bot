package com.aqualen.subscriptionchanneltelegrambot.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.TreeMap;

@Getter
@AllArgsConstructor
public enum Period {
    WEEK(7, "Неделя"),
    MONTH(31, "Месяц"),
    FOREVER(-1, "Навсегда");

    final int days;
    final String name;

    public static final String CHOOSE_PERIOD_MESSAGE = "Выберите период подписки:";

    public static final Map<Period, String> subscriptionPeriod = new TreeMap<>(Map.of(
            WEEK, WEEK.getName(),
            MONTH, MONTH.getName(),
            FOREVER, FOREVER.getName()));
}
