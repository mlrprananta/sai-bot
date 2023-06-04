package com.mlrp.saibot.commands.f1;

import com.mlrp.saibot.commands.Subcommand;
import com.mlrp.saibot.commands.SubcommandGroup;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ResultsSubcommandGroup extends SubcommandGroup<F1Command> {

  public ResultsSubcommandGroup(List<Subcommand<ResultsSubcommandGroup>> subcommands) {
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
