package com.mlrp.saibot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PingCommand implements Command<ChatInputInteractionEvent> {
  @Override
  public String getCommandName() {
    return "ping";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return event.reply("Pong!");
  }
}
