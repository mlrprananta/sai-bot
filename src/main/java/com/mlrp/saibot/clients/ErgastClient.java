package com.mlrp.saibot.clients;

import com.mlrp.saibot.clients.domain.ergast.RaceTable;
import com.mlrp.saibot.clients.domain.ergast.Response;
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
        .mapNotNull(r -> r.MRData().raceTable())
        .cache();
  }

  public Mono<RaceTable> fetchQualifyingResult() {
    return client
        .get()
        .uri(uriBuilder -> uriBuilder.path("/current.json").build())
        .retrieve()
        .bodyToMono(Response.class)
        .mapNotNull(r -> r.MRData().raceTable())
        .cache();
  }
}
