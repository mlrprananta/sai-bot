package com.mlrprananta.snapp.clients.domain.ergast;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConstructorStanding(
    int position, int points, int wins, @JsonProperty("Constructor") Constructor constructor) {}
