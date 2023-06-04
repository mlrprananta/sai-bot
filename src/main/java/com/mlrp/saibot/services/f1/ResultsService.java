package com.mlrp.saibot.services.f1;

import static com.mlrp.saibot.services.f1.ResultsService.SessionType.RACE;

import com.mlrp.saibot.clients.ErgastClient;
import com.mlrp.saibot.clients.domain.ergast.Race;
import com.mlrp.saibot.clients.domain.ergast.Result;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ResultsService {
  private final ErgastClient client;
  private final ScheduleService scheduleService;
  private final Clock clock;

  public ResultsService(ErgastClient client, ScheduleService scheduleService, Clock clock) {
    this.client = client;
    this.scheduleService = scheduleService;
    this.clock = clock;
  }

  public Mono<Results> getLastRaceResults() {
    return client
        .fetchLastRaceResults()
        .filter(race -> race.results().isPresent())
        .map(race -> new Results(race.raceName(), RACE, race.results().orElseThrow()));
  }

  public Mono<Results> getLastResult() {
    return client
        .fetchLastRaceResults()
        .filter(race -> race.results().isPresent())
        .map(race -> new Results(race.raceName(), RACE, race.results().orElseThrow()));
  }

  public Mono<QualifyingResults> getLastQualifyingResults() {
    return scheduleService
        .getLastQualifyingSession()
        .flatMap(session -> client.fetchQualifyingResults(session.round()))
        .filter(race -> race.qualifyingResults().isPresent())
        .map(ResultsService::toQualifyingResults);
  }

  private static QualifyingResults toQualifyingResults(Race race) {
    return new QualifyingResults(
        race.raceName(),
        race.qualifyingResults().orElseThrow().stream()
            .map(
                result ->
                    new QualifyingResult(
                        result.driver().familyName(),
                        result.position(),
                        result.q1(),
                        result.q2(),
                        result.q3()))
            .toList());
  }

  public record Results(String raceName, SessionType sessionType, List<Result> results) {}

  public record RaceResult(String familyName, int position, int points, String time) {}

  public record QualifyingResults(String raceName, List<QualifyingResult> results) {}

  public record QualifyingResult(
      String familyName,
      int position,
      Optional<String> q1,
      Optional<String> q2,
      Optional<String> q3) {
    public String getTime() {
      return Stream.of(q3.stream(), q2.stream(), q1.stream())
          .flatMap(Function.identity())
          .findFirst()
          .orElseThrow();
    }
  }

  public enum SessionType {
    RACE,
    QUALIFYING
  }
}
