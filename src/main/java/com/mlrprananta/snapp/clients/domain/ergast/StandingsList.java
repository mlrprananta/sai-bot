package com.mlrprananta.snapp.clients.domain.ergast;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Optional;

public record StandingsList(
    String season,
    int round,
    @JsonProperty("DriverStandings") Optional<List<DriverStanding>> driverStandings,
    @JsonProperty("ConstructorStandings")
        Optional<List<ConstructorStanding>> constructorStandings) {}
