package com.mlrp.saibot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
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
  public ApplicationCommandRequest getCommandRequest() {
    return ApplicationCommandRequest.builder()
        .name(getCommandName())
        .description("Do you need help?")
        .build();
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return event
        .reply()
        .withEmbeds(toEmbedCreateSpec(commands.stream().map(Command::getCommandRequest).toList()));
  }

  private EmbedCreateSpec toEmbedCreateSpec(List<ApplicationCommandRequest> list) {
    return EmbedCreateSpec.builder()
        .title("Available Commands")
        .addAllFields(
            list.stream()
                .map(
                    request ->
                        EmbedCreateFields.Field.of(
                            request.name(), request.description().get(), false))
                .toList())
        .timestamp(Instant.now())
        .build();
  }
}
