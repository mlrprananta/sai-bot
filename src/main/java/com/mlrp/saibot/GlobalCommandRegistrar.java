package com.mlrp.saibot;

import com.mlrp.saibot.commands.SlashCommand;
import discord4j.rest.RestClient;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class GlobalCommandRegistrar implements ApplicationRunner {
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalCommandRegistrar.class);
  private final RestClient client;
  private final List<SlashCommand> commands;
  private final Long guildId;

  public GlobalCommandRegistrar(
      RestClient restClient, List<SlashCommand> commands, @Value("${my.guild.id}") Long guildId) {
    this.client = restClient;
    this.commands = commands;
    this.guildId = guildId;
  }

  @Override
  public void run(ApplicationArguments args) {
    client
        .getApplicationId()
        .flatMap(
            id ->
                client
                    .getApplicationService()
                    .bulkOverwriteGlobalApplicationCommand(
                        id, commands.stream().map(SlashCommand::getCommandRequest).toList())
                    .doOnNext(
                        commandData ->
                            LOGGER.info(
                                "'{}' command has been updated or registered.", commandData.name()))
                    .then())
        .block();
  }
}
