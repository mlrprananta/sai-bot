package com.mlrp.saibot.commands.formula1.subcommands;

import com.mlrp.saibot.clients.ErgastClient;
import com.mlrp.saibot.commands.Subcommand;
import com.mlrp.saibot.commands.formula1.Formula1Command;
import com.mlrp.saibot.services.Formula1SessionsService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateFields.Field;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import java.time.Instant;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class NextSubcommand extends Subcommand<Formula1Command> {
  public static final Color COLOR = Color.of(225, 6, 0);
  private final Formula1SessionsService sessionsService;

  public NextSubcommand(Formula1SessionsService sessionsService) {
    this.sessionsService = sessionsService;
  }

  @Override
  public String getCommandName() {
    return "next";
  }

  @Override
  public String getDescription() {
    return "Get info on the next F1 sessions.";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return sessionsService
        .getCurrentRace()
        .flatMap(
            race ->
                event
                    .reply()
                    .withEmbeds(toEmbedCreateSpec(race))
                    .withComponents(
                        ActionRow.of(Button.link("https://f1tv.formula1.com/", "Watch on F1TV"))));
  }

  private static EmbedCreateSpec toEmbedCreateSpec(ErgastClient.Race race) {
    return EmbedCreateSpec.create()
        .withTitle(race.raceName())
        .withDescription(race.circuit().circuitName())
        .withThumbnail("https://upload.wikimedia.org/wikipedia/commons/f/f2/New_era_F1_logo.png")
        .withColor(COLOR)
        .withFields(
            race.getSessions().stream()
                .filter(session -> session.instant().isAfter(Instant.now()))
                .map(
                    session ->
                        Field.of(
                            session.name(),
                            "<t:%s>".formatted(session.instant().getEpochSecond()),
                            false))
                .toList());
  }
}
