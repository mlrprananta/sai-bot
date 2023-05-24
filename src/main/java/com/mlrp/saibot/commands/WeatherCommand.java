package com.mlrp.saibot.commands;

import static discord4j.core.object.command.ApplicationCommandOption.Type.STRING;

import com.fasterxml.jackson.annotation.JsonProperty;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

@Component
public class WeatherCommand implements Command<ChatInputInteractionEvent> {
  private static final String LOCATION_OPTION = "location";
  private static final String UNITS_OPTION = "units";
  private final String key;
  private final WebClient webClient;

  public WeatherCommand(
      WebClient.Builder webClientBuilder,
      @Value("${weather.url}") String url,
      @Value("${weather.key}") String key) {
    this.key = key;
    this.webClient = webClientBuilder.baseUrl(url).build();
  }

  @Override
  public String getCommandName() {
    return "weather";
  }

  @Override
  public ApplicationCommandRequest getCommandRequest() {
    return ApplicationCommandRequest.builder()
        .name(getCommandName())
        .description("Get the weather for a location!")
        .addOption(
            ApplicationCommandOptionData.builder()
                .name(LOCATION_OPTION)
                .description("The location (e.g. Amsterdam)")
                .type(STRING.getValue())
                .required(true)
                .build())
        .build();
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return webClient
        .get()
        .uri(uriBuilder -> buildUri(event, uriBuilder))
        .retrieve()
        .bodyToMono(ResponseDTO.class)
        .map(WeatherCommand::toEmbedCreateSpec)
        .flatMap(embed -> event.reply().withEmbeds(embed))
        .doOnError(t -> LOGGER.error("Weather command failed.", t))
        .onErrorResume(__ -> event.reply("No weather data found!"));
  }

  private URI buildUri(ChatInputInteractionEvent event, UriBuilder uriBuilder) {
    return uriBuilder
        .path("/weather")
        .queryParam("q", getOption(event, LOCATION_OPTION).orElse(""))
        .queryParam("units", "metric")
        .queryParam("appid", this.key)
        .build();
  }

  private static EmbedCreateSpec toEmbedCreateSpec(ResponseDTO response) {
    Summary summary = response.summaries().stream().findFirst().orElseThrow();
    return EmbedCreateSpec.builder()
        .color(Color.of(234, 110, 75))
        .title("Weather in " + response.name())
        .thumbnail("http://openweathermap.org/img/wn/" + summary.icon() + "@2x.png")
        .description(StringUtils.capitalize(summary.description()))
        .addField("Temperature", response.temperatureData().temp() + "°C", false)
        .footer("Powered by OpenWeather", "")
        .build();
  }

  private static Optional<String> getOption(ChatInputInteractionEvent event, String name) {
    return event
        .getOption(name)
        .flatMap(ApplicationCommandInteractionOption::getValue)
        .map(ApplicationCommandInteractionOptionValue::asString);
  }

  private record ResponseDTO(
      @JsonProperty("coord") Coordinates coordinates,
      @JsonProperty("weather") List<Summary> summaries,
      @JsonProperty("main") TemperatureData temperatureData,
      @JsonProperty("wind") WindData windData,
      String name) {}

  private record Coordinates(double lon, double lat) {}

  private record Summary(int id, String main, String description, String icon) {}

  private record TemperatureData(double temp) {}

  private record WindData(double speed) {}
}
