package com.mlrprananta.snapp.clients;

import com.mlrprananta.snapp.clients.domain.ergast.*;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ErgastClient {
  private final WebClient client;

  public ErgastClient(WebClient.Builder builder, @Value("${f1.url}") String url) {
    this.client = builder.baseUrl(url).build();
  }

  public Mono<RaceTable> fetchRaceTable() {
    return getResponse("/current.json").transform(ErgastClient::getRaceTable);
  }

  public Mono<Race> fetchLastQualifyingResults() {
    return getResponse("/current/last/qualifying.json")
        .transform(ErgastClient::getRace)
        .cache(Duration.ofHours(1));
  }

  public Mono<Race> fetchQualifyingResults(int round) {
    return getResponse("/current/" + round + "/qualifying.json")
        .transform(ErgastClient::getRace)
        .cache(Duration.ofHours(1));
  }

  public Mono<Race> fetchLastRaceResults() {
    return getResponse("/current/last/results.json")
        .transform(ErgastClient::getRace)
        .cache(Duration.ofHours(1));
  }

  public Mono<Race> fetchRaceResults(int round) {
    return getResponse("/current/" + round + "/qualifying.json")
        .transform(ErgastClient::getRace)
        .cache(Duration.ofHours(1));
  }

  public Mono<StandingsList> fetchDriverStandings() {
    return getResponse("/current/driverStandings.json")
        .transform(ErgastClient::getStandingList)
        .cache(Duration.ofHours(1));
  }

  public Mono<StandingsList> fetchConstructorStandings() {
    return getResponse("/current/constructorStandings.json")
        .transform(ErgastClient::getStandingList)
        .cache(Duration.ofHours(1));
  }

  private Mono<Response> getResponse(String path) {
    return client
        .get()
        .uri(uriBuilder -> uriBuilder.path(path).build())
        .retrieve()
        .bodyToMono(Response.class)
        .timeout(Duration.ofSeconds(2));
  }

  private static Mono<StandingsList> getStandingList(Mono<Response> response) {
    return response
        .map(Response::MRData)
        .flatMapIterable(data -> data.standingsTable().standingsLists())
        .next();
  }

  private static Mono<RaceTable> getRaceTable(Mono<Response> response) {
    return response.map(Response::MRData).map(MRData::raceTable);
  }

  private static Mono<Race> getRace(Mono<Response> response) {
    return getRaceTable(response).flatMapIterable(RaceTable::races).next();
  }
}
