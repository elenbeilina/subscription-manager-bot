package com.aqualen.subscriptionchanneltelegrambot.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("bot")
public class BotProperties {
  private String username;
  private String token;
}
