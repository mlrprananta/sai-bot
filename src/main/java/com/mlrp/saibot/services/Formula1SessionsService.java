package com.mlrp.saibot.services;

import com.mlrp.saibot.clients.ErgastClient;
import com.mlrp.saibot.clients.ErgastClient.Race;
import com.mlrp.saibot.clients.ErgastClient.RaceTable;
import java.time.Clock;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class Formula1SessionsService {
  private final Mono<RaceTable> raceTable;
  private final Clock clock;

  public Formula1SessionsService(ErgastClient client, Clock clock) {
    this.clock = clock;
    this.raceTable = client.fetchRaceTable().cache();
  }

  public Mono<RaceTable> getRaceTable() {
    return raceTable;
  }

  public Mono<Race> getCurrentRace() {
    return Mono.fromSupplier(clock::instant)
        .flatMap(
            now ->
                raceTable
                    .flatMapIterable(RaceTable::races)
                    .filter(r -> r.getInstant().isAfter(now))
                    .next());
  }
}
