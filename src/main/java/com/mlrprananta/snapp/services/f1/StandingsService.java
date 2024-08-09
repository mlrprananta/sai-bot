package com.mlrprananta.snapp.services.f1;

import com.mlrprananta.snapp.clients.ErgastClient;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class StandingsService {
  private final ErgastClient client;

  public StandingsService(ErgastClient client) {
    this.client = client;
  }

  public Mono<ConstructorStandings> getConstructorStandings() {
    return client
        .fetchConstructorStandings()
        .map(
            standingsList ->
                new ConstructorStandings(
                    standingsList.season(),
                    standingsList.constructorStandings().stream()
                        .flatMap(Collection::stream)
                        .map(StandingsService::toConstructorStanding)
                        .toList()));
  }

  public Mono<DriverStandings> getDriverStandings() {
    return client
        .fetchDriverStandings()
        .map(
            standingsList ->
                new DriverStandings(
                    standingsList.season(),
                    standingsList.driverStandings().stream()
                        .flatMap(Collection::stream)
                        .map(StandingsService::toDriverStanding)
                        .toList()));
  }

  private static ConstructorStanding toConstructorStanding(
      com.mlrprananta.snapp.clients.domain.ergast.ConstructorStanding standing) {
    return new ConstructorStanding(
        standing.constructor().name(), standing.position(), standing.wins(), standing.points());
  }

  private static DriverStanding toDriverStanding(
      com.mlrprananta.snapp.clients.domain.ergast.DriverStanding driverStanding) {
    return new DriverStanding(
        driverStanding.driver().familyName(),
        driverStanding.position(),
        driverStanding.wins(),
        driverStanding.points());
  }

  public record ConstructorStandings(String season, List<ConstructorStanding> standings) {}

  public record ConstructorStanding(String constructor, int position, int wins, int points) {}

  public record DriverStandings(String season, List<DriverStanding> standings) {}

  public record DriverStanding(String familyName, int position, int wins, int points) {}
}
