package com.mlrprananta.snapp.commands;

import static java.util.function.UnaryOperator.identity;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.discordjson.json.ApplicationCommandRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class SlashCommand extends AbstractCommand {
  protected final Map<String, Subcommand<?>> subcommands;

  public SlashCommand() {
    this.subcommands = Map.of();
  }

  public <T extends AbstractCommand> SlashCommand(List<Subcommand<T>> subcommands) {
    this.subcommands =
        subcommands.stream().collect(Collectors.toMap(Subcommand::getCommandName, identity()));
  }

  public ApplicationCommandRequest getCommandRequest() {
    return ApplicationCommandRequest.builder()
        .name(getCommandName())
        .description(getDescription())
        .addAllOptions(subcommands.values().stream().map(Subcommand::getCommandOptionData).toList())
        .build();
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return Flux.fromIterable(event.getOptions())
        .map(ApplicationCommandInteractionOption::getName)
        .singleOrEmpty()
        .filter(subcommands::containsKey)
        .mapNotNull(subcommands::get)
        .doOnNext(subcommand -> logInteraction(event, subcommand))
        .flatMap(subcommand -> subcommand.handle(event));
  }

  private void logInteraction(ChatInputInteractionEvent event, AbstractCommand command) {
    event
        .getInteraction()
        .getMember()
        .ifPresent(
            member ->
                LOGGER.info(
                    "'{} {}' command was invoked by '{}'.",
                    this.getCommandName(),
                    command.getCommandName(),
                    member.getDisplayName()));
  }
}
