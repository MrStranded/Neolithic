package engine.parser.scripts.execution.commands;

import engine.parser.scripts.execution.Command;

import java.util.List;

public interface CommandProvider {

    List<Command> buildCommands();

}
