package com.mlrp.saibot.clients.domain.ergast;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Circuit(
    String circuitId,
    String url,
    String circuitName,
    @JsonProperty("Location") Location location) {}
