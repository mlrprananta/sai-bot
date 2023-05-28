package com.mlrp.saibot.clients.domain.ergast;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalTime;
import java.util.Optional;

public record QualifyingResult(
    String number,
    String position,
    @JsonProperty("Driver") Driver driver,
    @JsonProperty("Constructor") Constructor constructor,
    @JsonProperty("Q1") Optional<LocalTime> q1,
    @JsonProperty("Q2") Optional<LocalTime> q2,
    @JsonProperty("Q3") Optional<LocalTime> q3) {}
