package com.mlrp.saibot.services;

import static java.util.function.Function.identity;

import com.mlrp.saibot.clients.ErgastClient;
import com.mlrp.saibot.clients.ErgastClient.Race;
import com.mlrp.saibot.clients.ErgastClient.RaceTable;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class Formula1ScheduleService {
  private final Mono<RaceTable> raceTable;
  private final Clock clock;

  public Formula1ScheduleService(ErgastClient client, Clock clock) {
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

  public static List<Session> getSessions(Race race) {
    return Stream.of(
            Stream.of(
                new Session("Free Practice 1", race.freePractice1().getInstant()),
                new Session("Free Practice 2", race.freePractice2().getInstant()),
                new Session("Qualifying", race.qualifying().getInstant()),
                new Session("Grand Prix", race.getInstant())),
            race.freePractice3().stream()
                .map(session -> new Session("Free Practice 3", session.getInstant())),
            race.sprint().stream().map(session -> new Session("Sprint", session.getInstant())))
        .flatMap(identity())
        .sorted(Comparator.comparing(Session::instant, Instant::compareTo))
        .toList();
  }

  public record Session(String name, Instant instant) {}
}
