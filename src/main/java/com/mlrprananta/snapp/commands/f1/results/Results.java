package com.mlrprananta.snapp.commands.f1.results;

import com.mlrprananta.snapp.commands.Subcommand;
import com.mlrprananta.snapp.commands.SubcommandGroup;
import com.mlrprananta.snapp.commands.f1.BaseCommand;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class Results extends SubcommandGroup<BaseCommand> {

  public Results(List<Subcommand<Results>> subcommands) {
    super(subcommands);
  }

  @Override
  public String getCommandName() {
    return "results";
  }

  @Override
  public String getDescription() {
    return "Get various F1 results";
  }
}
