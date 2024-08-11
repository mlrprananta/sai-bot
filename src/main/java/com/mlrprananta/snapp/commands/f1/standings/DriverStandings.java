package com.mlrprananta.snapp.commands.f1.standings;

import static com.mlrprananta.snapp.commands.f1.BaseCommand.COLOR;

import com.mlrprananta.snapp.commands.Subcommand;
import com.mlrprananta.snapp.services.f1.StandingsService;
import com.mlrprananta.snapp.services.f1.StandingsService.DriverStanding;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import java.util.List;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DriverStandings extends Subcommand<Standings> {

  private final StandingsService service;

  public DriverStandings(StandingsService service) {
    this.service = service;
  }

  @Override
  public String getCommandName() {
    return "driver";
  }

  @Override
  public String getDescription() {
    return "Get the driver standings of the current season!";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return event
        .deferReply()
        .then(service.getDriverStandings())
        .map(this::toInteractionFollowup)
        .flatMap(event::createFollowup)
        .then();
  }

  private InteractionFollowupCreateSpec toInteractionFollowup(
      StandingsService.DriverStandings driverStandings) {
    return InteractionFollowupCreateSpec.builder()
        .addEmbed(toEmbedCreateSpec(driverStandings))
        .build();
  }

  private EmbedCreateSpec toEmbedCreateSpec(StandingsService.DriverStandings driverStandings) {
    List<DriverStanding> standings = driverStandings.standings();
    return EmbedCreateSpec.builder()
        .color(COLOR)
        .addField("Driver Standings - " + driverStandings.season(), "", false)
        .addAllFields(standings.stream().map(DriverStandings::toField).toList())
        .build();
  }

  private static EmbedCreateFields.Field toField(DriverStanding standing) {
    return EmbedCreateFields.Field.of(getRow(standing), "", false);
  }

  private static String getRow(DriverStanding standing) {
    return String.format(
        "`%-2s %-24s %4s`",
        standing.position(),
        String.format(
            "%s %s",
            standing.familyName(), standing.wins() > 0 ? "(" + standing.wins() + " wins)" : ""),
        standing.points());
  }
}
