package com.mlrp.saibot.commands.formula1;

import static com.mlrp.saibot.services.Formula1ScheduleService.getSessions;

import com.mlrp.saibot.clients.domain.ergast.Race;
import com.mlrp.saibot.commands.Subcommand;
import com.mlrp.saibot.services.Formula1ScheduleService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateFields.Field;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class Formula1NextSubcommand extends Subcommand<Formula1Command> {
  public static final Color COLOR = Color.of(225, 6, 0);
  private final Formula1ScheduleService sessionsService;
  private final Clock clock;

  public Formula1NextSubcommand(Formula1ScheduleService sessionsService, Clock clock) {
    this.sessionsService = sessionsService;
    this.clock = clock;
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
                    .withEmbeds(toEmbedCreateSpec(race, clock.instant()))
                    .withComponents(
                        ActionRow.of(Button.link("https://f1tv.formula1.com/", "Watch on F1TV"))));
  }

  private static EmbedCreateSpec toEmbedCreateSpec(Race race, Instant now) {
    return EmbedCreateSpec.create()
        .withTitle(race.raceName())
        .withDescription(race.circuit().circuitName())
        .withColor(COLOR)
        .withFields(
            getSessions(race).stream()
                .filter(session -> session.instant().isAfter(now))
                .map(
                    session ->
                        Field.of(
                            session.name(),
                            "<t:%s:R>".formatted(session.instant().getEpochSecond()),
                            false))
                .toList());
  }
}
