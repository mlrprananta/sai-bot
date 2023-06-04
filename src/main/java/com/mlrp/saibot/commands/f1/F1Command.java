package com.mlrp.saibot.commands.f1;

import com.mlrp.saibot.commands.SlashCommand;
import com.mlrp.saibot.commands.Subcommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import java.util.List;
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
    return super.handle(event)
        .onErrorResume(
            throwable ->
                event
                    .reply()
                    .withEmbeds(
                        EmbedCreateSpec.create()
                            .withColor(Color.of(225, 6, 0))
                            .withTitle("Ergast API Offline")
                            .withDescription("Please check again later!")));
  }
}
