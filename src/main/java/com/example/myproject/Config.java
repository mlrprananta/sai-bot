package com.example.myproject;

import com.example.myproject.handlers.EventHandler;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.Event;
import discord4j.rest.RestClient;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

@Configuration
public class Config {
  private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
  private final String token;

  public Config(@Value("${bot.token}") String token) {
    this.token = token;
  }

  @Bean
  public <E extends Event> GatewayDiscordClient client(List<EventHandler<E>> eventHandlers) {
    return DiscordClientBuilder.create(token)
        .build()
        .gateway()
        .withEventDispatcher(
            eventDispatcher -> setUpEventDispatcher(eventHandlers, eventDispatcher))
        .setAwaitConnections(true)
        .login()
        .block();
  }

  @Bean
  public RestClient restClient(GatewayDiscordClient client) {
    return client.getRestClient();
  }

  private static <E extends Event> Flux<Void> setUpEventDispatcher(
      List<EventHandler<E>> eventHandlers, EventDispatcher eventDispatcher) {
    return Flux.fromIterable(eventHandlers)
        .flatMap(
            handler ->
                eventDispatcher
                    .on(handler.getEventType())
                    .flatMap(handler::handle)
                    .doOnSubscribe(
                        __ ->
                            LOGGER.info(
                                "Ready to handle {}", handler.getEventType().getSimpleName())));
  }
}
