package com.mlrp.saibot.commands.formula1;

import static discord4j.core.object.command.ApplicationCommandOption.Type.SUB_COMMAND;
import static java.util.function.UnaryOperator.identity;

import com.mlrp.saibot.commands.SlashCommand;
import com.mlrp.saibot.commands.Subcommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class Formula1Command extends SlashCommand {

  private final Map<String, Subcommand<Formula1Command>> subcommands;

  public Formula1Command(List<Subcommand<Formula1Command>> subcommands) {
    this.subcommands =
        subcommands.stream().collect(Collectors.toMap(Subcommand::getCommandName, identity()));
  }

  @Override
  public String getCommandName() {
    return "f1";
  }

  @Override
  public String getDescription() {
    return "Anything Formula 1.";
  }

  @Override
  public ApplicationCommandRequest getCommandRequest() {
    return ApplicationCommandRequest.builder()
        .name(getCommandName())
        .description(getDescription())
        .addAllOptions(
            subcommands.values().stream()
                .map(
                    subcommand ->
                        ApplicationCommandOptionData.builder()
                            .name(subcommand.getCommandName())
                            .description(subcommand.getDescription())
                            .type(SUB_COMMAND.getValue())
                            .build())
                .map(ApplicationCommandOptionData.class::cast)
                .toList())
        .build();
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return Flux.fromIterable(event.getOptions())
        .map(ApplicationCommandInteractionOption::getName)
        .singleOrEmpty()
        .filter(subcommands::containsKey)
        .mapNotNull(subcommands::get)
        .flatMap(subcommand -> subcommand.handle(event));
  }
}
