package com.mlrp.saibot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class HelpCommand extends SlashCommand {
  private final List<SlashCommand> commands;

  public HelpCommand(List<SlashCommand> commands) {
    this.commands = commands;
  }

  @Override
  public String getCommandName() {
    return "help";
  }

  @Override
  public String getDescription() {
    return "Do you need help?";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return event.reply().withEmbeds(toEmbedCreateSpec(commands));
  }

  private EmbedCreateSpec toEmbedCreateSpec(List<SlashCommand> list) {
    return EmbedCreateSpec.builder()
        .title("Available Commands")
        .addAllFields(
            list.stream()
                .map(
                    request ->
                        EmbedCreateFields.Field.of(
                            request.getCommandName(), request.getDescription(), false))
                .toList())
        .timestamp(Instant.now())
        .build();
  }
}
