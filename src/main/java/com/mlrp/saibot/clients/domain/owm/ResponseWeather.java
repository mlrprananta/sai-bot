package com.mlrp.saibot.clients.domain.owm;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ResponseWeather(
    @JsonProperty("coord") Coordinates coordinates,
    @JsonProperty("weather") List<Summary> summaries,
    @JsonProperty("main") TemperatureData temperatureData,
    @JsonProperty("wind") WindData windData,
    String name) {}
