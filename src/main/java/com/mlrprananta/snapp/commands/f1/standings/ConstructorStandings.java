package com.mlrprananta.snapp.commands.f1.standings;

import static com.mlrprananta.snapp.commands.f1.BaseCommand.COLOR;
import static com.mlrprananta.snapp.services.f1.StandingsService.*;

import com.mlrprananta.snapp.commands.Subcommand;
import com.mlrprananta.snapp.services.f1.StandingsService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import java.util.List;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ConstructorStandings extends Subcommand<Standings> {

  private final StandingsService service;

  public ConstructorStandings(StandingsService service) {
    this.service = service;
  }

  @Override
  public String getCommandName() {
    return "constructor";
  }

  @Override
  public String getDescription() {
    return "Get the constructor standings of the current season!";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return event
        .deferReply()
        .then(service.getConstructorStandings())
        .map(this::toInteractionFollowup)
        .flatMap(event::createFollowup)
        .then();
  }

  private InteractionFollowupCreateSpec toInteractionFollowup(
      StandingsService.ConstructorStandings c) {
    return InteractionFollowupCreateSpec.builder().addEmbed(toEmbedCreateSpec(c)).build();
  }

  private EmbedCreateSpec toEmbedCreateSpec(
      StandingsService.ConstructorStandings constructorStandings) {
    List<ConstructorStanding> standings = constructorStandings.standings();
    return EmbedCreateSpec.builder()
        .color(COLOR)
        .addField("Constructor Standings - " + constructorStandings.season(), "", false)
        .addAllFields(standings.stream().map(ConstructorStandings::toField).toList())
        .build();
  }

  private static EmbedCreateFields.Field toField(ConstructorStanding standing) {
    return EmbedCreateFields.Field.of(getRow(standing), "", false);
  }

  private static String getRow(ConstructorStanding standing) {
    return String.format(
        "`%-2s %-24s %4s`",
        standing.position(),
        String.format(
            "%s %s",
            standing.constructor(), standing.wins() > 0 ? "(" + standing.wins() + " wins)" : ""),
        standing.points());
  }
}
