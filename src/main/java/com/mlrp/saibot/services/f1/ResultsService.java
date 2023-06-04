package com.mlrp.saibot.services.f1;

import com.mlrp.saibot.clients.ErgastClient;
import com.mlrp.saibot.clients.domain.ergast.Result;
import java.time.Clock;
import java.util.List;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ResultsService {
  private final ErgastClient client;
  private final Clock clock;

  public ResultsService(ErgastClient client, Clock clock) {
    this.client = client;
    this.clock = clock;
  }

  public Mono<RaceResults> getLastRaceResults() {
    return client
        .fetchLastRaceResults()
        .filter(race -> race.results().isPresent())
        .map(race -> new RaceResults(race.raceName(), race.results().orElseThrow()));
  }

  public record RaceResults(String raceName, List<Result> results) {}
}
