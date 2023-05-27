package com.mlrp.saibot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;

public abstract class SlashCommand implements Command<ChatInputInteractionEvent> {
  public ApplicationCommandRequest getCommandRequest() {
    return ApplicationCommandRequest.builder()
        .name(getCommandName())
        .description(getDescription())
        .build();
  }
}
