package com.mlrp.saibot.services.f1;

import static java.time.ZoneOffset.UTC;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlrp.saibot.clients.ErgastClient;
import com.mlrp.saibot.clients.domain.ergast.Race;
import com.mlrp.saibot.clients.domain.ergast.Response;
import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest(classes = JacksonAutoConfiguration.class)
class ScheduleServiceTest {
  @Autowired private ObjectMapper mapper;

  @Test
  void getCurrentRace() throws IOException {
    ErgastClient client = mock(ErgastClient.class);
    Response response =
        mapper.readValue(new File("src/test/resources/response.json"), Response.class);
    Race expectedRace = response.MRData().raceTable().races().get(5);
    Clock clock = Clock.fixed(expectedRace.getInstant().minus(1, ChronoUnit.DAYS), UTC);
    when(client.fetchRaceTable()).thenReturn(Mono.just(response.MRData().raceTable()));
    new ScheduleService(client, clock)
        .getCurrentRace()
        .as(StepVerifier::create)
        .expectNext(expectedRace)
        .verifyComplete();
  }
}
