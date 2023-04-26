package com.aqualen.subscriptionchanneltelegrambot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Channel {
    String name;
    String displayName;
    Map<Period, Integer> subscriptionCost;

    public static String CHANNEL = "channel_";
}
