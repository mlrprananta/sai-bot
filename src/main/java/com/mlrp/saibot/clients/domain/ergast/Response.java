package com.mlrp.saibot.clients.domain.ergast;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Response(@JsonProperty("MRData") MRData MRData) {}
