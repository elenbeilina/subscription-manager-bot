package com.aqualen.subscriptionchanneltelegrambot.entity;

import com.aqualen.subscriptionchanneltelegrambot.enums.PaymentTypes;
import com.aqualen.subscriptionchanneltelegrambot.enums.Periods;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.aqualen.subscriptionchanneltelegrambot.entity.Channel.CHANNEL_PREFIX;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    Long userId;
    String username;
    Periods periods;
    String channelId;
    PaymentTypes paymentType;

    public void setChannelId(String channelId) {
        this.channelId = channelId.replace(CHANNEL_PREFIX, "");
    }
}
