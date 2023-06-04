package com.mlrp.saibot.commands;

import static discord4j.core.object.command.ApplicationCommandOption.Type.SUB_COMMAND_GROUP;
import static java.util.function.UnaryOperator.identity;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class SubcommandGroup<P extends AbstractCommand> extends Subcommand<P> {
  private final Map<String, Subcommand<?>> subcommands;

  public <T extends AbstractCommand> SubcommandGroup(List<Subcommand<T>> subcommands) {
    this.subcommands =
        subcommands.stream().collect(Collectors.toMap(Command::getCommandName, identity()));
  }

  @Override
  public ApplicationCommandOptionData getCommandOptionData() {
    return ApplicationCommandOptionData.builder()
        .name(getCommandName())
        .description(getDescription())
        .type(SUB_COMMAND_GROUP.getValue())
        .addAllOptions(subcommands.values().stream().map(Subcommand::getCommandOptionData).toList())
        .build();
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return Flux.fromStream(
            event.getOptions().stream().flatMap(option -> option.getOptions().stream()))
        .map(ApplicationCommandInteractionOption::getName)
        .singleOrEmpty()
        .filter(subcommands::containsKey)
        .mapNotNull(subcommands::get)
        .flatMap(subcommand -> subcommand.handle(event));
  }
}
