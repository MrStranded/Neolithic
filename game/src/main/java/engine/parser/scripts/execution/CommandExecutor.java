package engine.parser.scripts.execution;

import engine.data.entities.Instance;
import engine.data.scripts.Script;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.commands.*;
import engine.parser.scripts.nodes.CommandExpressionNode;
import engine.parser.utils.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandExecutor {

	private static Map<String, Command> commands;

	public static void initialize() {
		commands = new HashMap<>();

		List<CommandProvider> providers = Arrays.asList(
				new ActionCommands(),
				new EntityManipulationCommands(),
				new GuiCommands(),
				new IteratorCommands(),
				new ListCommands(),
				new MathCommands(),
				new PlanetManipulationCommands(),
				new RetrievalCommands(),
				new ReturnCommands()
		);

		providers.forEach(provider -> provider.buildCommands().forEach(
				command -> commands.put(command.getName(), command)
		));
	}
	
	public static Variable executeCommand(Instance self, Script script, CommandExpressionNode commandNode) throws ScriptInterruptedException {
		if (commands == null) {
			Logger.error("Commands have not been initialized! -> CommandExecutor.initialize()");
			return new Variable();
		}

		String commandName = commandNode.getCommand().getValue();
		Variable[] parameters = ParameterCalculator.calculateParameters(self, script, commandNode);

		Command command = commands.get(commandName);
		if (command != null) {
			return command.execute(self, parameters);

		} else {
			Logger.error(
					"Unknown command '" + commandNode.getCommand() + "'!" +
					" Error on line " + commandNode.getCommand().getLine() +
					" during execution of script '" + script.getTextId() + "' in file '" + script.getFileName() + "'."
			);
        }

		return new Variable();
	}

}
