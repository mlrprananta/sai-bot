package com.mlrprananta.snapp.handlers;

import com.mlrprananta.snapp.commands.SlashCommand;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
final class ChatInputInteractionEventHandler extends BaseEventHandler<ChatInputInteractionEvent> {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(ChatInputInteractionEventHandler.class);
  private final List<SlashCommand> commands;

  public ChatInputInteractionEventHandler(
      GatewayDiscordClient gatewayClient, List<SlashCommand> commands) {
    super(gatewayClient, ChatInputInteractionEvent.class);
    this.commands = commands;
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return Flux.fromIterable(commands)
        .filter(command -> event.getCommandName().equals(command.getCommandName()))
        .doOnNext(command -> logInteraction(event, command))
        .flatMap(c -> c.handle(event))
        .doOnError(ChatInputInteractionEventHandler::logError)
        .onErrorComplete()
        .then();
  }

  private static void logInteraction(ChatInputInteractionEvent event, SlashCommand command) {
    event
        .getInteraction()
        .getMember()
        .ifPresent(
            member ->
                LOGGER.info(
                    "'{}' command was performed by '{}'.",
                    command.getCommandName(),
                    member.getDisplayName()));
  }

  private static void logError(Throwable t) {
    LOGGER.error("Command failed.", t);
  }
}
