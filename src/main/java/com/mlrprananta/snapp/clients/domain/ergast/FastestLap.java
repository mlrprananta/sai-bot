package com.mlrprananta.snapp.clients.domain.ergast;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FastestLap(
    int rank,
    int lap,
    @JsonProperty("Time") Time time,
    @JsonProperty("AverageSpeed") AverageSpeed averageSpeed) {}
