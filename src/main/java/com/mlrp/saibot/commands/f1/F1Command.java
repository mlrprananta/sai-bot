package com.mlrp.saibot.commands.f1;

import com.mlrp.saibot.commands.SlashCommand;
import com.mlrp.saibot.commands.Subcommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono;
import discord4j.rest.util.Color;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class F1Command extends SlashCommand {
  public static final Color COLOR = Color.of(225, 6, 0);

  public F1Command(List<Subcommand<F1Command>> subcommands) {
    super(subcommands);
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
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return super.handle(event).onErrorResume(throwable -> handleError(event, throwable));
  }

  private static InteractionApplicationCommandCallbackReplyMono handleError(
      ChatInputInteractionEvent event, Throwable throwable) {
    return event
        .reply()
        .withEmbeds(
            throwable instanceof TimeoutException
                ? EmbedCreateSpec.create()
                    .withColor(COLOR)
                    .withTitle("Ergast API Offline")
                    .withDescription("Please check again later!")
                : EmbedCreateSpec.create()
                    .withColor(COLOR)
                    .withTitle("Error")
                    .withDescription("Oh no."));
  }
}
