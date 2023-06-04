package com.mlrp.saibot.clients;

import com.mlrp.saibot.clients.domain.ergast.*;
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
    return client
        .get()
        .uri(uriBuilder -> uriBuilder.path("/current.json").build())
        .retrieve()
        .bodyToMono(Response.class)
        .transform(this::getRaceTable)
        .cache();
  }

  public Mono<Race> fetchLastQualifyingResults() {
    return client
        .get()
        .uri(uriBuilder -> uriBuilder.path("/current/last/qualifying.json").build())
        .retrieve()
        .bodyToMono(Response.class)
        .transform(this::getRace)
        .cache(Duration.ofHours(1));
  }

  public Mono<Race> fetchQualifyingResults(int round) {
    return client
        .get()
        .uri(uriBuilder -> uriBuilder.path("/current/" + round + "/qualifying.json").build())
        .retrieve()
        .bodyToMono(Response.class)
        .transform(this::getRace)
        .cache(Duration.ofHours(1));
  }

  public Mono<Race> fetchLastRaceResults() {
    return client
        .get()
        .uri(uriBuilder -> uriBuilder.path("/current/last/results.json").build())
        .retrieve()
        .bodyToMono(Response.class)
        .transform(this::getRace)
        .cache(Duration.ofHours(1));
  }

  public Mono<Race> fetchRaceResults(int round) {
    return client
        .get()
        .uri(uriBuilder -> uriBuilder.path("/current/" + round + "/qualifying.json").build())
        .retrieve()
        .bodyToMono(Response.class)
        .transform(this::getRace)
        .cache(Duration.ofHours(1));
  }

  private Mono<RaceTable> getRaceTable(Mono<Response> response) {
    return response.map(Response::MRData).map(MRData::raceTable);
  }

  private Mono<Race> getRace(Mono<Response> response) {
    return getRaceTable(response).flatMapIterable(RaceTable::races).next();
  }
}
