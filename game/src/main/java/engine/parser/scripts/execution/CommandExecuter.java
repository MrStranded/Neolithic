package engine.parser.scripts.execution;

import constants.ScriptConstants;
import engine.data.entities.Instance;
import engine.data.planetary.Face;
import engine.data.planetary.Planet;
import engine.data.planetary.Tile;
import engine.data.proto.Container;
import engine.data.proto.Data;
import engine.data.variables.Variable;
import engine.parser.utils.Logger;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.nodes.AbstractScriptNode;
import engine.parser.scripts.nodes.CommandExpressionNode;
import engine.parser.tokenization.Token;

import java.util.ArrayList;
import java.util.List;

public class CommandExecuter {

	public static Variable executeCommand(Instance self, CommandExpressionNode commandNode) {
		System.out.println("execute command: " + commandNode.getCommand());

		Token command = commandNode.getCommand();
		Variable[] parameters = ParameterCalculator.calculateParameters(self, commandNode);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& createFormation (String formationTextID, Tile tile)
		if (TokenConstants.CREATE_FORMATION.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				String formationTextID = parameters[0].getString();
				int formationID = Data.getContainerID(formationTextID);
				Tile tile = parameters[1].getTile();

				if (formationID >= 0 && tile != null) {
					Instance formationInstance = new Instance(formationID);
					Variable[] newParameters = new Variable[1];
					newParameters[0] = parameters[1];
					formationInstance.runScript(ScriptConstants.EVENT_PLACE_FORMATION, newParameters);
				} else {
					if (formationID == -1) {
						Logger.error("Cannot create Formation: Formation '" + formationTextID + "' does not exist. Line " + command.getLine());
					}
					if (tile == null) {
						Logger.error("Cannot create Formation: Not a valid tile value.");
					}
				}
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& random ([double bottom,] double top)
		} else if (TokenConstants.RANDOM.equals(command)) {
			if (parameters.length >= 2) {
				double bottom = parameters[0].getDouble();
				double top = parameters[1].getDouble();

				double value = bottom + Math.random() * (top - bottom);
				return new Variable(value);
			} else if (requireParameters(commandNode, 1)) {
				double top = parameters[0].getDouble();

				double value = Math.random() * top;
				return new Variable(value);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& randomTile ()
		} else if (TokenConstants.RANDOM_TILE.equals(command)) {
			Planet planet = Data.getPlanet();
			if (planet != null) {
				int f = (int) (Math.random() * 20d);
				int x = (int) (Math.random() * planet.getSize());
				int y = (int) (Math.random() * planet.getSize());

				Tile tile = planet.getFace(f).getTile(x,y);
				return new Variable(tile);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& setHeight (int height)
		} else if (TokenConstants.SET_HEIGHT.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				Tile tile = parameters[0].getTile();
				int height = parameters[1].getInt();

				if (tile != null) {
					tile.setHeight(height);
					if (Data.getPlanet() != null) {
						Data.getPlanet().updatePlanetMesh();
					}
				}
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& setWaterLevel (int level)
		} else if (TokenConstants.SET_WATER_LEVEL.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				Planet planet = Data.getPlanet();
				if (planet != null) {
					int level = (int) (parameters[0].getDouble());
					for (Face face : planet.getFaces()) {
						for (Tile tile : face.getTiles()) {
							tile.setWaterHeight(level);
						}
					}
					planet.updatePlanetMesh();
				}
			}
		}

		return new Variable();
	}

	private static boolean requireParameters(CommandExpressionNode commandNode, int amount) {
		if (amount > 0 && (commandNode.getSubNodes() == null || amount > commandNode.getSubNodes().length)) {
			String errorMessage = "The command '" + commandNode.getCommand().getValue()
					+ "' needs at least " + amount
					+ " parameters on line " + commandNode.getCommand().getLine();
			Logger.error(errorMessage);
			return false;
		}
		return true;
	}

}
