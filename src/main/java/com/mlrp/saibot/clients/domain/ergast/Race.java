package com.mlrp.saibot.clients.domain.ergast;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Optional;

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
