package com.aqualen.subscriptionchanneltelegrambot.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Period {
    WEEK(7, "Неделя"),
    MONTH(31, "Месяц"),
    FOREVER(-1, "Навсегда");

    final int days;
    final String name;
}
