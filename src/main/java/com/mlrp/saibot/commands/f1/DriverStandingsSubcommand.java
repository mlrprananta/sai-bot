package com.mlrp.saibot.commands.f1;

import static com.mlrp.saibot.commands.f1.F1Command.COLOR;

import com.mlrp.saibot.commands.Subcommand;
import com.mlrp.saibot.services.f1.StandingsService;
import com.mlrp.saibot.services.f1.StandingsService.DriverStanding;
import com.mlrp.saibot.services.f1.StandingsService.DriverStandings;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import java.util.List;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DriverStandingsSubcommand extends Subcommand<StandingsSubcommandGroup> {

  private final StandingsService service;

  public DriverStandingsSubcommand(StandingsService service) {
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
    return service
        .getDriverStandings()
        .map(this::toEmbedCreateSpec)
        .switchIfEmpty(
            Mono.just(
                EmbedCreateSpec.create()
                    .withColor(COLOR)
                    .withTitle("Data Unavailable")
                    .withDescription("Check back later!")))
        .flatMap(event.reply()::withEmbeds);
  }

  private EmbedCreateSpec toEmbedCreateSpec(DriverStandings driverStandings) {
    List<DriverStanding> standings = driverStandings.standings();
    return EmbedCreateSpec.builder()
        .color(COLOR)
        .addField("Driver Standings - " + driverStandings.season(), "", false)
        .addAllFields(standings.stream().map(DriverStandingsSubcommand::toField).toList())
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
