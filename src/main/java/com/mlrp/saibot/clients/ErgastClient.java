package com.mlrp.saibot.clients;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
        .mapNotNull(r -> r.data().raceTable())
        .cache();
  }

  public record Response(@JsonProperty("MRData") Data data) {}

  public record Data(@JsonProperty("RaceTable") RaceTable raceTable) {}

  public record RaceTable(String season, @JsonProperty("Races") List<Race> races) {}

  public record Race(
      String season,
      int round,
      String url,
      String raceName,
      @JsonProperty("Circuit") Circuit circuit,
      String date,
      String time,
      @JsonProperty("FirstPractice") Session freePractice1,
      @JsonProperty("SecondPractice") Session freePractice2,
      @JsonProperty("ThirdPractice") Optional<Session> freePractice3,
      @JsonProperty("Sprint") Optional<Session> sprint,
      @JsonProperty("Qualifying") Session qualifying) {
    public Instant getInstant() {
      return Instant.parse(date + "T" + time);
    }
  }

  public record Circuit(
      String circuitId,
      String url,
      String circuitName,
      @JsonProperty("Location") Location location) {}

  private record Location(
      @JsonProperty("lat") double latitude,
      @JsonProperty("long") double longitude,
      String locality,
      String country) {}

  public record Session(String date, String time) {
    public Instant getInstant() {
      return Instant.parse(date + "T" + time);
    }
  }
}
