package com.mlrp.saibot.clients.domain.ergast;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;

public record QualifyingResult(
    int number,
    int position,
    @JsonProperty("Driver") Driver driver,
    @JsonProperty("Constructor") Constructor constructor,
    @JsonProperty("Q1") Optional<String> q1,
    @JsonProperty("Q2") Optional<String> q2,
    @JsonProperty("Q3") Optional<String> q3) {}
