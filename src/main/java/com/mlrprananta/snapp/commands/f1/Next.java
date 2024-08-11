package com.mlrprananta.snapp.commands.f1;

import static com.mlrprananta.snapp.commands.f1.BaseCommand.COLOR;
import static com.mlrprananta.snapp.services.f1.ScheduleService.getNextSession;

import com.mlrprananta.snapp.clients.domain.ergast.Race;
import com.mlrprananta.snapp.commands.Subcommand;
import com.mlrprananta.snapp.services.f1.ScheduleService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateFields.Field;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class Next extends Subcommand<BaseCommand> {
  private final ScheduleService sessionsService;
  private final Clock clock;

  public Next(ScheduleService sessionsService, Clock clock) {
    this.sessionsService = sessionsService;
    this.clock = clock;
  }

  @Override
  public String getCommandName() {
    return "next";
  }

  @Override
  public String getDescription() {
    return "Get info on the next F1 session.";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return event
        .deferReply()
        .then(sessionsService.getCurrentRace())
        .map(this::toInteractionFollowupCreateSpec)
        .flatMap(event::createFollowup)
        .then();
  }

  private InteractionFollowupCreateSpec toInteractionFollowupCreateSpec(Race race) {
    return InteractionFollowupCreateSpec.builder()
        .addEmbed(toEmbedCreateSpec(race, clock.instant()))
        .addComponent(ActionRow.of(Button.link("https://f1tv.formula1.com/", "Watch on F1TV")))
        .build();
  }

  private static EmbedCreateSpec toEmbedCreateSpec(Race race, Instant now) {
    ScheduleService.Session session = getNextSession(race, now);
    return EmbedCreateSpec.create()
        .withTitle(race.raceName())
        .withDescription(race.circuit().circuitName())
        .withColor(COLOR)
        .withFields(
            List.of(
                Field.of(
                    session.name(),
                    "<t:%s:R>".formatted(session.instant().getEpochSecond()),
                    false)));
  }
}
