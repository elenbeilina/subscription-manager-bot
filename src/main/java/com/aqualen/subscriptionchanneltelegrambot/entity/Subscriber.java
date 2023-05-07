package com.aqualen.subscriptionchanneltelegrambot.entity;

import com.aqualen.subscriptionchanneltelegrambot.enums.Periods;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subscriber {

    private Long userId;
    private String userName;
    private Map<String, Channel> channels;

    public static Subscriber buildSubscriber(User user) {
        return Subscriber.builder()
                .userName(user.getUsername())
                .userId(user.getUserId())
                .channels(new HashMap<>(
                        Map.of(user.getChannelId(), Channel.buildChannel(user)))
                ).build();
    }

    public void addOrRenewSubscription(User user) {
        String channelId = user.getChannelId();
        if (channels.containsKey(channelId)) {
            Channel subscription = channels.get(channelId);
            if (Periods.FOREVER == user.getPeriods()) {
                subscription.setEndOfSubscriptionDate(LocalDate.MAX);
            }
            LocalDate renewedEndDate = subscription.getEndOfSubscriptionDate()
                    .plusDays(user.getPeriods().getDays());
            subscription.setEndOfSubscriptionDate(renewedEndDate);
            channels.put(channelId, subscription);
        } else {
            channels.put(channelId, Channel.buildChannel(user));
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Channel {
        private LocalDate endOfSubscriptionDate;
        private String provider;
        private String providerPaymentChargeId;

        public static Channel buildChannel(User user) {
            return Channel.builder()
                    .endOfSubscriptionDate(LocalDate.now().plusDays(user.getPeriods().getDays()))
                    .provider(user.getPaymentType().getName())
                    .providerPaymentChargeId(user.getProviderPaymentChargeId()).build();
        }
    }

}
