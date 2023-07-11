package com.mlrp.saibot.commands.f1;

import static com.mlrp.saibot.commands.f1.F1Command.COLOR;
import static com.mlrp.saibot.services.f1.ScheduleService.getSessions;

import com.mlrp.saibot.clients.domain.ergast.Race;
import com.mlrp.saibot.commands.Subcommand;
import com.mlrp.saibot.services.f1.ScheduleService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SessionsSubcommand extends Subcommand<F1Command> {
  private final ScheduleService sessionsService;
  private final Clock clock;

  public SessionsSubcommand(ScheduleService sessionsService, Clock clock) {
    this.sessionsService = sessionsService;
    this.clock = clock;
  }

  @Override
  public String getCommandName() {
    return "sessions";
  }

  @Override
  public String getDescription() {
    return "Get info on upcoming F1 sessions.";
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
                .map(
                    session ->
                        EmbedCreateFields.Field.of(
                            session.name(),
                            "<t:%s>".formatted(session.instant().getEpochSecond()),
                            false))
                .toList());
  }
}
