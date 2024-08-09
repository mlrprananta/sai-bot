package com.mlrp.saibot.handlers;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public abstract class BaseEventHandler<E extends Event> {
  private static final Logger LOGGER = LoggerFactory.getLogger(BaseEventHandler.class);

  public BaseEventHandler(GatewayDiscordClient gatewayClient, Class<E> eventClass) {
    gatewayClient
        .on(eventClass, this::handle)
        .doOnSubscribe(__ -> LOGGER.info("Ready to handle {}", eventClass.getSimpleName()))
        .subscribe();
  }

  abstract Mono<Void> handle(E event);
}
