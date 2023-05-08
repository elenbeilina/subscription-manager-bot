package com.aqualen.subscriptionchanneltelegrambot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.TreeMap;

@Getter
@AllArgsConstructor
public enum Periods {
    WEEK(7, "Week"),
    MONTH(31, "Month"),
    FOREVER(-1, "Forever");

    public static final String CHOOSE_PERIOD_MESSAGE = "Choose subscription period:";
    public static final Map<Periods, String> subscriptionPeriod = new TreeMap<>(Map.of(
            WEEK, WEEK.getName(),
            MONTH, MONTH.getName(),
            FOREVER, FOREVER.getName()));
    
    final int days;
    final String name;
}
