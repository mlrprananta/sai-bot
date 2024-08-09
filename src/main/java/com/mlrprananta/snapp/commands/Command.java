package com.mlrprananta.snapp.commands;

import discord4j.core.event.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

interface Command<E extends Event> {
  Logger LOGGER = LoggerFactory.getLogger(Command.class);

  String getCommandName();

  String getDescription();

  Mono<Void> handle(E event);
}
