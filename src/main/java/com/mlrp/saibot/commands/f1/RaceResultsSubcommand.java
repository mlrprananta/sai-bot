package com.mlrp.saibot.commands.f1;

import static com.mlrp.saibot.commands.f1.F1Command.COLOR;

import com.mlrp.saibot.clients.domain.ergast.Result;
import com.mlrp.saibot.clients.domain.ergast.Time;
import com.mlrp.saibot.commands.Subcommand;
import com.mlrp.saibot.services.f1.ResultsService;
import com.mlrp.saibot.services.f1.ResultsService.Results;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import java.util.List;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RaceResultsSubcommand extends Subcommand<ResultsSubcommandGroup> {
  private final ResultsService service;

  public RaceResultsSubcommand(ResultsService service) {
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
    return service
        .getLastRaceResults()
        .map(this::toEmbedCreateSpec)
        .switchIfEmpty(
            Mono.just(
                EmbedCreateSpec.create()
                    .withColor(COLOR)
                    .withTitle("Data Unavailable")
                    .withDescription("Check back later!")))
        .flatMap(event.reply()::withEmbeds);
  }

  private EmbedCreateSpec toEmbedCreateSpec(Results raceResults) {
    List<Result> results = raceResults.results();
    return EmbedCreateSpec.create()
        .withColor(Color.of(225, 6, 0))
        .withTitle("Race Results")
        .withDescription(raceResults.raceName())
        .withFields(results.stream().map(RaceResultsSubcommand::toField).toList());
  }

  private static EmbedCreateFields.Field toField(Result result) {
    return EmbedCreateFields.Field.of(getTitle(result), "", false);
  }

  private static String getTitle(Result result) {
    return String.format(
        "`%-2s %-16s %12s `",
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
