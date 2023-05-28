package com.mlrp.saibot.clients.domain.ergast;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Location(
    @JsonProperty("lat") double latitude,
    @JsonProperty("long") double longitude,
    String locality,
    String country) {}
