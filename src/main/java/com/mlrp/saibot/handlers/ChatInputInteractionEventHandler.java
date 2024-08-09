package com.mlrp.saibot.handlers;

import com.mlrp.saibot.commands.SlashCommand;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import java.util.List;
import java.util.Optional;
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
    Optional<Member> member = event.getInteraction().getMember();
    return Flux.fromIterable(commands)
        .filter(command -> event.getCommandName().equals(command.getCommandName()))
        .doOnNext(
            command ->
                LOGGER.info(
                    "'{}' command was performed by '{}'.",
                    command.getCommandName(),
                    member.map(Member::getDisplayName).orElse("Bot")))
        .flatMap(c -> c.handle(event))
        .doOnError(t -> LOGGER.error("Command failed.", t))
        .onErrorComplete()
        .then();
  }
}
