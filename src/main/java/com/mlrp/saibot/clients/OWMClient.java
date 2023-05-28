package com.mlrp.saibot.clients;

import com.mlrp.saibot.clients.domain.owm.ResponseWeather;
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

  public Mono<ResponseWeather> fetchWeatherForLocation(String location) {
    return webClient
        .get()
        .uri(uriBuilder -> getBaseUriBuilder(uriBuilder).queryParam("q", location).build())
        .retrieve()
        .bodyToMono(ResponseWeather.class);
  }

  public Mono<ResponseWeather> fetchWeatherForCoordinates(double latitude, double longitude) {
    return webClient
        .get()
        .uri(
            uriBuilder ->
                getBaseUriBuilder(uriBuilder)
                    .queryParam("lat", latitude)
                    .queryParam("lon", longitude)
                    .build())
        .retrieve()
        .bodyToMono(ResponseWeather.class);
  }

  private UriBuilder getBaseUriBuilder(UriBuilder uriBuilder) {
    return uriBuilder
        .path("/weather")
        .queryParam("units", "metric")
        .queryParam("appid", this.apiKey);
  }
}
