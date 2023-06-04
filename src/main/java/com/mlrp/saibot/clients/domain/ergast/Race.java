package com.mlrp.saibot.clients.domain.ergast;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record Race(
    String season,
    int round,
    String url,
    String raceName,
    @JsonProperty("Circuit") Circuit circuit,
    String date,
    String time,
    @JsonProperty("QualifyingResults") Optional<QualifyingResults> qualifyingResults,
    @JsonProperty("Results") Optional<List<Result>> results,
    @JsonProperty("FirstPractice") Optional<Session> freePractice1,
    @JsonProperty("SecondPractice") Optional<Session> freePractice2,
    @JsonProperty("ThirdPractice") Optional<Session> freePractice3,
    @JsonProperty("Sprint") Optional<Session> sprint,
    @JsonProperty("Qualifying") Optional<Session> qualifying) {
  public Instant getInstant() {
    return Instant.parse(date + "T" + time);
  }

  public Map<String, Instant> getSessions() {
    Map<String, Instant> map = new HashMap<>();
    freePractice1.ifPresent(session -> map.put("Free Practice 1", session.getInstant()));
    freePractice2.ifPresent(session -> map.put("Free Practice 2", session.getInstant()));
    freePractice3.ifPresent(session -> map.put("Free Practice 3", session.getInstant()));
    qualifying.ifPresent(session -> map.put("Qualifying", session.getInstant()));
    sprint.ifPresent(session -> map.put("Sprint", session.getInstant()));
    map.put("Grand Prix", getInstant());
    return map;
  }
}
