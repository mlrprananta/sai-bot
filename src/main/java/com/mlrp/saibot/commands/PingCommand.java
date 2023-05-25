package com.mlrp.saibot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PingCommand extends SlashCommand {
  @Override
  public String getCommandName() {
    return "ping";
  }

  @Override
  public ApplicationCommandRequest getCommandRequest() {
    return ApplicationCommandRequest.builder()
        .name(getCommandName())
        .description("It's ping pong time.")
        .build();
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return event.reply("Pong!");
  }
}
