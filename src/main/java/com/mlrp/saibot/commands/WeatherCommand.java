package com.mlrp.saibot.commands;

import static discord4j.core.object.command.ApplicationCommandOption.Type.STRING;

import com.mlrp.saibot.clients.OWMClient;
import com.mlrp.saibot.clients.OWMClient.Response;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Component
public class WeatherCommand extends SlashCommand {
  private static final String LOCATION_OPTION = "location";
  private static final String UNITS_OPTION = "units";
  private final OWMClient client;

  public WeatherCommand(OWMClient client) {
    this.client = client;
  }

  @Override
  public String getCommandName() {
    return "weather";
  }

  @Override
  public String getDescription() {
    return "Get the weather for a location!";
  }

  @Override
  public ApplicationCommandRequest getCommandRequest() {
    return ApplicationCommandRequest.builder()
        .name(getCommandName())
        .description(getDescription())
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
    return client
        .fetchWeatherForLocation(getOption(event, LOCATION_OPTION).orElse(""))
        .map(WeatherCommand::toEmbedCreateSpec)
        .flatMap(embed -> event.reply().withEmbeds(embed))
        .doOnError(t -> LOGGER.error("Weather command failed.", t))
        .onErrorResume(__ -> event.reply("No weather data found!"));
  }

  private static EmbedCreateSpec toEmbedCreateSpec(Response response) {
    OWMClient.Summary summary = response.summaries().stream().findFirst().orElseThrow();
    return EmbedCreateSpec.builder()
        .color(Color.of(234, 110, 75))
        .title("Weather in " + response.name())
        .thumbnail("http://openweathermap.org/img/wn/" + summary.icon() + "@2x.png")
        .description(StringUtils.capitalize(summary.description()))
        .addField("Temperature", response.temperatureData().temp() + "°C", false)
        .footer("Powered by OpenWeatherMap", "")
        .build();
  }

  private static Optional<String> getOption(ChatInputInteractionEvent event, String name) {
    return event
        .getOption(name)
        .flatMap(ApplicationCommandInteractionOption::getValue)
        .map(ApplicationCommandInteractionOptionValue::asString);
  }
}
