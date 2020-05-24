package engine.parser.scripts.execution;

import constants.ResourcePathConstants;
import constants.ScriptConstants;
import constants.TopologyConstants;
import engine.data.Data;
import engine.data.options.GameOptions;
import engine.data.scripts.Script;
import engine.data.entities.Effect;
import engine.data.entities.Instance;
import engine.data.planetary.Face;
import engine.data.planetary.Planet;
import engine.data.planetary.Tile;
import engine.data.proto.Container;
import engine.data.proto.ProtoAttribute;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.logic.topology.Neighbour;
import engine.logic.topology.Pathfinding;
import engine.logic.topology.TopologyGenerator;
import engine.logic.topology.TileArea;
import engine.math.numericalObjects.Vector3;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.exceptions.InvalidValueException;
import engine.parser.scripts.exceptions.ReturnException;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.commands.*;
import engine.parser.scripts.nodes.CommandExpressionNode;
import engine.parser.tokenization.Token;
import engine.parser.utils.Logger;

import java.util.*;

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
			Logger.error("Command have not been initialized! -> CommandExecutor.initialize()");
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
