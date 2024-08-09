package com.mlrprananta.snapp;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.rest.RestClient;
import java.time.Clock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
  private final String token;

  public Config(@Value("${bot.token}") String token) {
    this.token = token;
  }

  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }

  @Bean
  public GatewayDiscordClient client() {
    return DiscordClientBuilder.create(token)
        .build()
        .gateway()
        .setAwaitConnections(true)
        .login()
        .block();
  }

  @Bean
  public RestClient restClient(GatewayDiscordClient client) {
    return client.getRestClient();
  }
}
