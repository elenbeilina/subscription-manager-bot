package com.aqualen.subscriptionchanneltelegrambot.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("payment")
public class PaymentProperties {
    String yoomoneyToken;
}
