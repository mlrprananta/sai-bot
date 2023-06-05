package com.mlrp.saibot.clients.domain.ergast;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record StandingsTable(
    String season, @JsonProperty("StandingsLists") List<StandingsList> standingsLists) {}
