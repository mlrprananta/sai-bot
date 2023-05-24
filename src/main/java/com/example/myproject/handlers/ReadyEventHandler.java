package com.example.myproject.handlers;

import discord4j.core.event.domain.lifecycle.ReadyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ReadyEventHandler implements EventHandler<ReadyEvent> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ReadyEventHandler.class);

  public ReadyEventHandler() {}

  @Override
  public Class<ReadyEvent> getEventType() {
    return ReadyEvent.class;
  }

  @Override
  public Mono<Void> handle(ReadyEvent event) {
    return Mono.fromRunnable(
        () -> LOGGER.info("{} is ready to roll.", event.getSelf().getUsername()));
  }
}
