package com.mlrprananta.snapp.commands.f1;

import static com.mlrprananta.snapp.commands.f1.BaseCommand.COLOR;
import static com.mlrprananta.snapp.services.f1.ScheduleService.getSessions;

import com.mlrprananta.snapp.clients.domain.ergast.Race;
import com.mlrprananta.snapp.commands.Subcommand;
import com.mlrprananta.snapp.services.f1.ScheduleService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import java.time.Clock;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class Sessions extends Subcommand<BaseCommand> {
  private final ScheduleService sessionsService;
  private final Clock clock;

  public Sessions(ScheduleService sessionsService, Clock clock) {
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
    return event
        .deferReply()
        .then(sessionsService.getCurrentRace())
        .map(this::toInteractionFollowup)
        .flatMap(event::createFollowup)
        .then();
  }

  private InteractionFollowupCreateSpec toInteractionFollowup(Race race) {
    return InteractionFollowupCreateSpec.builder()
        .addEmbed(toEmbed(race))
        .addComponent(ActionRow.of(Button.link("https://f1tv.formula1.com/", "Watch on F1TV")))
        .build();
  }

  private static EmbedCreateSpec toEmbed(Race race) {
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
