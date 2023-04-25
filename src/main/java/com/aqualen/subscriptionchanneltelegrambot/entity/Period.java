package com.aqualen.subscriptionchanneltelegrambot.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Period {
    WEEK(7),
    MONTH(31),
    FOREVER(-1);

    final int days;
}
