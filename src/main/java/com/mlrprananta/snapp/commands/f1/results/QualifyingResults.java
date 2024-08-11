package com.mlrprananta.snapp.commands.f1.results;

import static com.mlrprananta.snapp.commands.f1.BaseCommand.COLOR;
import static com.mlrprananta.snapp.services.f1.ResultsService.QualifyingSession.Q1;

import com.mlrprananta.snapp.commands.Subcommand;
import com.mlrprananta.snapp.services.f1.ResultsService;
import com.mlrprananta.snapp.services.f1.ResultsService.QualifyingSessionResult;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class QualifyingResults extends Subcommand<Results> {
  private final ResultsService service;

  public QualifyingResults(ResultsService service) {
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

  private static final String QUALIFYING_SELECT_MENU_ID = "qualifyingSelectMenuId";
  private static final SelectMenu SELECT_MENU =
      SelectMenu.of(
          QUALIFYING_SELECT_MENU_ID,
          SelectMenu.Option.of("Q1", "Q1"),
          SelectMenu.Option.of("Q2", "Q2"),
          SelectMenu.Option.of("Q3", "Q3"));

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return event
        .deferReply()
        .then(service.getLastQualifyingResults())
        .flatMap(
            results ->
                event
                    .createFollowup(toInteractionFollowup(results))
                    .flatMapMany(message -> listen(event.getClient(), results, message.getId()))
                    .onErrorResume(TimeoutException.class, __ -> event.deleteReply())
                    .then());
  }

  private InteractionFollowupCreateSpec toInteractionFollowup(ResultsService.QualifyingResults q) {
    return InteractionFollowupCreateSpec.builder()
        .addComponent(ActionRow.of(SELECT_MENU))
        .addEmbed(toEmbed(q, Q1))
        .build();
  }

  private Flux<Void> listen(
      GatewayDiscordClient client,
      ResultsService.QualifyingResults qualifyingResults,
      Snowflake messageId) {
    return client
        .on(SelectMenuInteractionEvent.class)
        .publishOn(Schedulers.boundedElastic())
        .filter(event -> event.getMessageId().equals(messageId))
        .filter(event -> event.getCustomId().equals(QUALIFYING_SELECT_MENU_ID))
        .flatMap(event -> handle(event, qualifyingResults, messageId))
        .timeout(Duration.ofMinutes(5));
  }

  private Mono<Void> handle(
      SelectMenuInteractionEvent event,
      ResultsService.QualifyingResults qualifyingResults,
      Snowflake messageId) {
    return event
        .deferEdit()
        .thenMany(Flux.fromIterable(event.getValues()))
        .next()
        .map(ResultsService.QualifyingSession::valueOf)
        .map(qualifyingSession -> toEmbed(qualifyingResults, qualifyingSession))
        .flatMap(embed -> event.editFollowup(messageId).withEmbeds(embed))
        .then();
  }

  private EmbedCreateSpec toEmbed(
      ResultsService.QualifyingResults qualifyingResults,
      ResultsService.QualifyingSession qualifyingSession) {
    List<QualifyingSessionResult> results =
        qualifyingResults.getQualifyingSessionResults(qualifyingSession);
    QualifyingSessionResult pole = results.get(0);
    return EmbedCreateSpec.builder()
        .color(COLOR)
        .addField(qualifyingResults.raceName() + " - " + qualifyingSession.name(), "", false)
        .addAllFields(
            IntStream.range(0, results.size())
                .mapToObj(index -> toField(results.get(index), index + 1, pole))
                .toList())
        .build();
  }

  private static EmbedCreateFields.Field toField(
      QualifyingSessionResult result, int position, QualifyingSessionResult pole) {
    return EmbedCreateFields.Field.of(
        String.format(
            "`%-2s %-16s %12s`",
            position,
            result.familyName(),
            result.time().equals(pole.time())
                ? pole.getFormattedTime()
                : result.getDelta(pole.time())),
        "",
        false);
  }
}
