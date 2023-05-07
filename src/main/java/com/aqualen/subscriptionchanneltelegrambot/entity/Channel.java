package com.aqualen.subscriptionchanneltelegrambot.entity;

import com.aqualen.subscriptionchanneltelegrambot.enums.Periods;
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
    public static final String CHANNEL_PREFIX = "channel_";

    private String id;
    private String name;
    private Map<Periods, Integer> subscriptionCost;
}
