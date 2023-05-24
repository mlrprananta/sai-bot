package com.example.myproject.handlers;

import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;

public interface EventHandler<E extends Event> {
  Class<E> getEventType();

  Mono<Void> handle(E event);
}
