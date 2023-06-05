package com.mlrp.saibot.clients.domain.ergast;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DriverStanding(
    int position, int points, int wins, @JsonProperty("Driver") Driver driver) {}
