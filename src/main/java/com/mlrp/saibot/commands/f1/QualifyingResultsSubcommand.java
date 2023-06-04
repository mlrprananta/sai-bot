package com.mlrp.saibot.commands.f1;

import static com.mlrp.saibot.commands.f1.F1Command.COLOR;

import com.mlrp.saibot.commands.Subcommand;
import com.mlrp.saibot.services.f1.ResultsService;
import com.mlrp.saibot.services.f1.ResultsService.QualifyingResult;
import com.mlrp.saibot.services.f1.ResultsService.QualifyingResults;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import java.util.List;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class QualifyingResultsSubcommand extends Subcommand<ResultsSubcommandGroup> {
  private final ResultsService service;

  public QualifyingResultsSubcommand(ResultsService service) {
    this.service = service;
  }

  @Override
  public String getCommandName() {
    return "quali";
  }

  @Override
  public String getDescription() {
    return "Get the results of the last qualifying!";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return service
        .getLastQualifyingResults()
        .map(this::toEmbedCreateSpec)
        .switchIfEmpty(
            Mono.just(
                EmbedCreateSpec.create()
                    .withColor(COLOR)
                    .withTitle("Data Unavailable")
                    .withDescription("Check back later!")))
        .flatMap(event.reply()::withEmbeds);
  }

  private EmbedCreateSpec toEmbedCreateSpec(QualifyingResults qualifyingResults) {
    List<QualifyingResult> results = qualifyingResults.results();
    return EmbedCreateSpec.create()
        .withColor(Color.of(225, 6, 0))
        .withTitle("Qualifying Results")
        .withDescription(qualifyingResults.raceName())
        .withFields(results.stream().map(QualifyingResultsSubcommand::toField).toList());
  }

  private static EmbedCreateFields.Field toField(QualifyingResult result) {
    return EmbedCreateFields.Field.of(getTitle(result), "", false);
  }

  private static String getTitle(QualifyingResult result) {
    return String.format(
        "`%-2s %-16s %12s`", result.position(), result.familyName(), result.getTime());
  }
}
