package com.mlrp.saibot.commands;

import discord4j.core.event.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public interface Command<E extends Event> {
  Logger LOGGER = LoggerFactory.getLogger(Command.class);

  String getCommandName();

  Mono<Void> handle(E event);
}
