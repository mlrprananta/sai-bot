package com.mlrp.saibot.commands;

import static discord4j.core.object.command.ApplicationCommandOption.Type.SUB_COMMAND;

import discord4j.discordjson.json.ApplicationCommandOptionData;

public abstract class Subcommand<P extends AbstractCommand> extends AbstractCommand {
  public ApplicationCommandOptionData getCommandOptionData() {
    return ApplicationCommandOptionData.builder()
        .name(getCommandName())
        .description(getDescription())
        .type(SUB_COMMAND.getValue())
        .build();
  }
}
