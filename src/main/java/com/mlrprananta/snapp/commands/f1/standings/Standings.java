package com.mlrprananta.snapp.commands.f1.standings;

import com.mlrprananta.snapp.commands.Subcommand;
import com.mlrprananta.snapp.commands.SubcommandGroup;
import com.mlrprananta.snapp.commands.f1.BaseCommand;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class Standings extends SubcommandGroup<BaseCommand> {

  public Standings(List<Subcommand<Standings>> subcommands) {
    super(subcommands);
  }

  @Override
  public String getCommandName() {
    return "standings";
  }

  @Override
  public String getDescription() {
    return "Get the standings for the current season!";
  }
}
