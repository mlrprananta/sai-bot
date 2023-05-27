package com.mlrp.saibot.clients;

import static java.util.function.Function.identity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
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
      @JsonProperty("FirstPractice") SessionTime freePractice1,
      @JsonProperty("SecondPractice") SessionTime freePractice2,
      @JsonProperty("ThirdPractice") Optional<SessionTime> freePractice3,
      @JsonProperty("Sprint") Optional<SessionTime> sprint,
      @JsonProperty("Qualifying") SessionTime qualifying) {
    public Instant getInstant() {
      return Instant.parse(date + "T" + time);
    }

    public List<Session> getSessions() {
      return Stream.of(
              Stream.of(
                  new Session("Free Practice 1", freePractice1.getInstant()),
                  new Session("Free Practice 2", freePractice2.getInstant()),
                  new Session("Qualifying", qualifying.getInstant()),
                  new Session("Grand Prix", getInstant())),
              freePractice3.stream()
                  .map(session -> new Session("Free Practice 3", session.getInstant())),
              sprint.stream().map(session -> new Session("Sprint", session.getInstant())))
          .flatMap(identity())
          .sorted(Comparator.comparing(Session::instant, Instant::compareTo))
          .toList();
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

  public record Session(String name, Instant instant) {}

  public record SessionTime(String date, String time) {
    Instant getInstant() {
      return Instant.parse(date + "T" + time);
    }
  }
}
