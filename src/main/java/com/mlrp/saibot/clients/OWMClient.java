package com.mlrp.saibot.clients;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

@Component
public class OWMClient {
  private final WebClient webClient;
  private final String apiKey;

  public OWMClient(
      WebClient.Builder webClientBuilder,
      @Value("${weather.url}") String url,
      @Value("${weather.api-key}") String apiKey) {
    this.apiKey = apiKey;
    this.webClient = webClientBuilder.baseUrl(url).build();
  }

  public Mono<Response> fetchWeatherForLocation(String location) {
    return webClient
        .get()
        .uri(uriBuilder -> getBaseUriBuilder(uriBuilder).queryParam("q", location).build())
        .retrieve()
        .bodyToMono(Response.class);
  }

  public Mono<Response> fetchWeatherForCoordinates(double latitude, double longitude) {
    return webClient
        .get()
        .uri(
            uriBuilder ->
                getBaseUriBuilder(uriBuilder)
                    .queryParam("lat", latitude)
                    .queryParam("lon", longitude)
                    .build())
        .retrieve()
        .bodyToMono(Response.class);
  }

  private UriBuilder getBaseUriBuilder(UriBuilder uriBuilder) {
    return uriBuilder
        .path("/weather")
        .queryParam("units", "metric")
        .queryParam("appid", this.apiKey);
  }

  public record Response(
      @JsonProperty("coord") Coordinates coordinates,
      @JsonProperty("weather") List<Summary> summaries,
      @JsonProperty("main") TemperatureData temperatureData,
      @JsonProperty("wind") WindData windData,
      String name) {}

  private record Coordinates(double lon, double lat) {}

  public record Summary(int id, String main, String description, String icon) {}

  public record TemperatureData(double temp) {}

  private record WindData(double speed) {}
}
