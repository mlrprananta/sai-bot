package com.mlrp.saibot.commands;

import discord4j.core.event.domain.Event;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public interface Command<E extends Event> {
  Logger LOGGER = LoggerFactory.getLogger(Command.class);

  String getCommandName();

  ApplicationCommandRequest getCommandRequest();

  Mono<Void> handle(E event);
}
