package com.aqualen.subscriptionchanneltelegrambot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    String username;
    Period period;
    String channelName;
}
