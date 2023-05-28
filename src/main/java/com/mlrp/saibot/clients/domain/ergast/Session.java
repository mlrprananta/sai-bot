package com.mlrp.saibot.clients.domain.ergast;

import java.time.Instant;

public record Session(String date, String time) {
  public Instant getInstant() {
    return Instant.parse(date + "T" + time);
  }
}
