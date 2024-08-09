package com.mlrprananta.snapp.clients.domain.ergast;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public record Time(Optional<Integer> millis, String time) {
  public LocalTime getLocalTime() {
    return LocalTime.parse(time, DateTimeFormatter.ofPattern("H:mm:ss.SSS"));
  }
}
