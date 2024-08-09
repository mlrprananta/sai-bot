package com.mlrprananta.snapp.services.f1;

import com.mlrprananta.snapp.clients.ErgastClient;
import com.mlrprananta.snapp.clients.domain.ergast.Race;
import com.mlrprananta.snapp.clients.domain.ergast.Result;
import java.time.Clock;
import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ResultsService {
  private final ErgastClient client;
  private final ScheduleService scheduleService;

  public ResultsService(ErgastClient client, ScheduleService scheduleService, Clock clock) {
    this.client = client;
    this.scheduleService = scheduleService;
  }

  public Mono<Results> getLastRaceResults() {
    return client
        .fetchLastRaceResults()
        .filter(race -> race.results().isPresent())
        .map(race -> new Results(race.raceName(), race.results().orElseThrow()));
  }

  public Mono<Results> getLastResult() {
    return client
        .fetchLastRaceResults()
        .filter(race -> race.results().isPresent())
        .map(race -> new Results(race.raceName(), race.results().orElseThrow()));
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
                        getQualifyingTimes(result)))
            .toList());
  }

  private static Map<QualifyingSession, Duration> getQualifyingTimes(
      com.mlrprananta.snapp.clients.domain.ergast.QualifyingResult result) {
    Map<QualifyingSession, Duration> map = new HashMap<>();
    result.q1().ifPresent(time -> map.put(QualifyingSession.Q1, parseDuration(time)));
    result.q2().ifPresent(time -> map.put(QualifyingSession.Q2, parseDuration(time)));
    result.q3().ifPresent(time -> map.put(QualifyingSession.Q3, parseDuration(time)));
    return map;
  }

  private static Duration parseDuration(String time) {
    Pattern regex = Pattern.compile("^(\\d+):(\\d+)\\.(\\d+)$");
    Matcher matcher = regex.matcher(time);
    if (matcher.matches()) {
      int minutes = Integer.parseInt(matcher.group(1));
      int seconds = Integer.parseInt(matcher.group(2));
      int milliseconds = Integer.parseInt(matcher.group(3));
      return Duration.ofMillis((minutes * 60L + seconds) * 1000L + milliseconds);
    }
    return Duration.ZERO;
  }

  public record Results(String raceName, List<Result> results) {}

  public record RaceResult(String familyName, int position, int points, String time) {}

  public record QualifyingResults(String raceName, List<QualifyingResult> results) {
    public List<QualifyingSessionResult> getQualifyingSessionResults(
        QualifyingSession qualifyingSession) {
      return results.stream()
          .filter(
              qualifyingResult -> qualifyingResult.qualifyingTimes().containsKey(qualifyingSession))
          .map(
              qualifyingResult ->
                  new QualifyingSessionResult(
                      qualifyingResult.familyName(),
                      qualifyingResult.qualifyingTimes().get(qualifyingSession)))
          .sorted(Comparator.comparing(QualifyingSessionResult::time))
          .toList();
    }
  }

  public record QualifyingResult(
      String familyName, int position, Map<QualifyingSession, Duration> qualifyingTimes) {}

  public record QualifyingSessionResult(String familyName, Duration time) {
    public String getFormattedTime() {
      long minutes = time.toMinutes();
      long seconds = time.minusMinutes(minutes).getSeconds();
      long milliseconds = time.toMillis() % 1000;
      return String.format("%d:%02d.%03d", minutes, seconds, milliseconds);
    }

    public String getDelta(Duration topTime) {
      return "+" + ((time.toNanos() - topTime.toNanos()) / 1e9);
    }
  }

  public enum QualifyingSession {
    Q1,
    Q2,
    Q3
  }
}
