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

    private Long userId;
    private String username;
    private Periods periods;
    private String channelId;
    private PaymentTypes paymentType;
    private String providerPaymentChargeId;

    public void setChannelId(String channelId) {
        this.channelId = channelId.replace(CHANNEL_PREFIX, "");
    }
}
