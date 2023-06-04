package com.mlrp.saibot.clients.domain.ergast;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;

public record Result(
    int number,
    int position,
    String positionText,
    int points,
    @JsonProperty("Driver") Driver driver,
    @JsonProperty("Constructor") Constructor constructor,
    int grid,
    int laps,
    String status,
    @JsonProperty("Time") Optional<Time> time,
    @JsonProperty("FastestLap") FastestLap fastestLap) {}
