package com.mlrprananta.snapp.services.f1;

import com.mlrprananta.snapp.clients.ErgastClient;
import com.mlrprananta.snapp.clients.domain.ergast.Race;
import com.mlrprananta.snapp.clients.domain.ergast.RaceTable;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ScheduleService {
  private final Mono<RaceTable> raceTable;
  private final Clock clock;

  public ScheduleService(ErgastClient client, Clock clock) {
    this.clock = clock;
    this.raceTable = client.fetchRaceTable().cache();
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

  public Mono<Session> getCurrentSession() {
    return getCurrentRace()
        .flatMapIterable(ScheduleService::getSessions)
        .filter(r -> r.instant().isAfter(clock.instant()))
        .next();
  }

  public Mono<Race> getLastRace() {
    return raceTable
        .flatMapIterable(RaceTable::races)
        .filter(r -> r.getInstant().isBefore(clock.instant()))
        .last();
  }

  public Mono<Session> getLastQualifyingSession() {
    return raceTable
        .flatMapIterable(RaceTable::races)
        .filter(race -> race.qualifying().isPresent())
        .filter(race -> race.qualifying().orElseThrow().getInstant().isBefore(clock.instant()))
        .last()
        .map(
            race ->
                new Session(
                    race.raceName(), race.round(), race.qualifying().orElseThrow().getInstant()));
  }

  public static List<Session> getSessions(Race race) {
    return race.getSessions().entrySet().stream()
        .map(entry -> new Session(entry.getKey(), race.round(), entry.getValue()))
        .sorted(Comparator.comparing(Session::instant))
        .toList();
  }

  public static Session getNextSession(Race race, Instant now) {
    return getSessions(race).stream()
        .filter(session -> session.instant().isAfter(now))
        .findFirst()
        .orElseThrow();
  }

  public record Session(String name, int round, Instant instant) {}
}
