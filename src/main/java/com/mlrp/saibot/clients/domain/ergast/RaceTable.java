package com.mlrp.saibot.clients.domain.ergast;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record RaceTable(String season, @JsonProperty("Races") List<Race> races) {}
