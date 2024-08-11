package com.mlrprananta.snapp.commands.f1.results;

import static com.mlrprananta.snapp.commands.f1.BaseCommand.COLOR;

import com.mlrprananta.snapp.clients.domain.ergast.Result;
import com.mlrprananta.snapp.clients.domain.ergast.Time;
import com.mlrprananta.snapp.commands.Subcommand;
import com.mlrprananta.snapp.services.f1.ResultsService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import java.util.List;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RaceResults extends Subcommand<Results> {
  private final ResultsService service;

  public RaceResults(ResultsService service) {
    this.service = service;
  }

  @Override
  public String getCommandName() {
    return "race";
  }

  @Override
  public String getDescription() {
    return "Get the results of the most recent grand prix!";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return event
        .deferReply()
        .then(service.getLastRaceResults())
        .map(this::toInteractionFollowup)
        .flatMap(event::createFollowup)
        .then();
  }

  private InteractionFollowupCreateSpec toInteractionFollowup(ResultsService.Results results) {
    return InteractionFollowupCreateSpec.builder().addEmbed(toEmbed(results)).build();
  }

  private EmbedCreateSpec toEmbed(ResultsService.Results raceResults) {
    List<Result> results = raceResults.results();
    return EmbedCreateSpec.builder()
        .color(COLOR)
        .addField(raceResults.raceName(), "", false)
        .addAllFields(results.stream().map(RaceResults::toField).toList())
        .build();
  }

  private static EmbedCreateFields.Field toField(Result result) {
    return EmbedCreateFields.Field.of(getTitle(result), "", false);
  }

  private static String getTitle(Result result) {
    return String.format(
        "`%-2s %-16s %12s`",
        result.position(),
        String.format(
            "%s %s",
            result.driver().familyName(),
            (result.points() > 0 ? "(+" + result.points() + ")" : "")),
        formatTime(result));
  }

  private static String formatTime(Result result) {
    return result
        .time()
        .map(Time::time)
        .orElse(result.status().startsWith("+") ? result.status() : "DNF");
  }

  private static String getPosition(int position) {
    return switch (position) {
      case 1 -> "\uD83E\uDD47";
      case 2 -> "\uD83E\uDD48";
      case 3 -> "\uD83E\uDD49";
      default -> String.valueOf(position);
    };
  }
}
