package com.mlrp.saibot.clients.domain.ergast;

import java.time.LocalDate;

public record Driver(
    String driverId,
    String permanentNumber,
    String code,
    String url,
    String givenName,
    String familyName,
    LocalDate dateOfBirth,
    String nationality) {}
