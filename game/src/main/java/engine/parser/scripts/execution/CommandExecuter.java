package engine.parser.scripts.execution;

import constants.ScriptConstants;
import constants.TopologyConstants;
import engine.data.entities.Instance;
import engine.data.planetary.Face;
import engine.data.planetary.Planet;
import engine.data.planetary.Tile;
import engine.data.Data;
import engine.data.structures.Script;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.logic.Neighbour;
import engine.logic.TopologyGenerator;
import engine.parser.utils.Logger;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.nodes.CommandExpressionNode;
import engine.parser.tokenization.Token;

public class CommandExecuter {

	public static Variable executeCommand(Instance self, Script script, CommandExpressionNode commandNode) {
		//System.out.println("execute command: " + commandNode.getCommand());

		Token command = commandNode.getCommand();
		Variable[] parameters = ParameterCalculator.calculateParameters(self, script, commandNode);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance createFormation (String formationTextID, Tile tile)
		if (TokenConstants.CHANCE.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				double chance = parameters[0].getDouble();

				return new Variable(Math.random() < chance ? 1 : 0);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance create (String textID, Tile tile)
		} else if (TokenConstants.CREATE.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				String textID = parameters[0].getString();
				int id = Data.getContainerID(textID);
				Tile tile = parameters[1].getTile();

				if (id >= 0 && tile != null) {
					Instance instance = new Instance(id);
					instance.setPosition(tile);
					Variable[] newParameters = new Variable[1];
					newParameters[0] = parameters[1];

					instance.runScript(ScriptConstants.EVENT_PLACE, newParameters);
					if (Data.getContainer(id).getType() == DataType.CREATURE) {
						Data.addCreatureToQueue(instance);
					}

					return new Variable(instance);
				} else {
					if (id == -1) {
						Logger.error("Cannot create Instance: Template for '" + textID + "' does not exist. Line " + command.getLine());
					}
					if (tile == null) {
						Logger.error("Cannot create Instance: Not a valid tile value.");
					}
				}
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int fitTiles ()
		} else if (TokenConstants.FIT_TILES.equals(command)) {
			if (Data.getPlanet() != null) {
				TopologyGenerator.fitTiles(Data.getPlanet());
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int getAttribute ([Instance instance,] String attribute)
		} else if (TokenConstants.GET_ATTRIBUTE.equals(command)) {
			if (parameters.length >= 2) {
				Instance instance = parameters[0].getInstance();

				String attributeTextID = parameters[1].getString();
				int attributeID = Data.getProtoAttributeID(attributeTextID);

				if (instance != null && attributeID >= 0) {
					return new Variable(instance.getAttribute(attributeID));
				} else {
					if (instance == null) {
						Logger.error("Instance value for command '" + TokenConstants.GET_ATTRIBUTE.getValue() + "' is invalid!");
					}
					if (attributeID == -1) {
						Logger.error("Attribute '" + attributeTextID + "' does not exist!");
					}
				}
			} else if (requireParameters(commandNode, 1)) {
				String attributeTextID = parameters[0].getString();
				int attributeID = Data.getProtoAttributeID(attributeTextID);

				if (attributeID >= 0) {
					return new Variable(self.getAttribute(attributeID));
				} else {
					Logger.error("Attribute '" + attributeTextID + "' does not exist!");
				}
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int getHeight (Tile tile)
		} else if (TokenConstants.GET_HEIGHT.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				Tile tile = parameters[0].getTile();

				if (tile == null) {
					return new Variable(0);
				}

				return new Variable(tile.getHeight());
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& tile getNeighbor (Tile tile, int position)
		} else if (TokenConstants.GET_NEIGHBOUR.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				Tile tile = parameters[0].getTile();
				int position = parameters[1].getInt();

				if (tile == null) {
					Logger.error("Tile value for command '" + TokenConstants.GET_NEIGHBOUR.getValue() + "' is invalid on line " + command.getLine());
					return new Variable();
				}
				if (position < 0 || position > 2) {
					position = position % 3;
				}

				return new Variable(Neighbour.getNeighbour(tile, position));
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& tile getTile (Instance instance)
		} else if (TokenConstants.GET_TILE.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				Instance instance = parameters[0].getInstance();

				if (instance == null) {
					Logger.error("Instance value for command '" + TokenConstants.GET_TILE.getValue() + "' is invalid on line " + command.getLine());
					return new Variable();
				}

				return new Variable(instance.getPosition());
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& tile moveTo (Instance instance, Tile tile)
		} else if (TokenConstants.MOVE_TO.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				Instance instance = parameters[0].getInstance();
				Tile tile = parameters[1].getTile();

				if (tile == null) {
					Logger.error("Tile value for command '" + TokenConstants.MOVE_TO.getValue() + "' is invalid on line " + command.getLine());
					return new Variable();
				}
				if (instance == null) {
					Logger.error("Instance value for command '" + TokenConstants.MOVE_TO.getValue() + "' is invalid on line " + command.getLine());
					return new Variable();
				}

				instance.setPosition(tile);
				return new Variable(tile);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& string print (String text)
		} else if (TokenConstants.PRINT.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				String text = parameters[0].getString();
				Logger.log("PRINT: " + text);
				return new Variable(text);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int random ([int bottom,] int top)
		} else if (TokenConstants.RANDOM.equals(command)) {
			if (parameters.length >= 2) {
				double bottom = parameters[0].getDouble();
				double top = parameters[1].getDouble();

				int value = (int) (bottom + Math.random() * (top - bottom));
				return new Variable(value);
			} else if (requireParameters(commandNode, 1)) {
				double top = parameters[0].getDouble();

				int value = (int) (Math.random() * top);
				return new Variable(value);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& tile randomTile ()
		} else if (TokenConstants.RANDOM_TILE.equals(command)) {
			Planet planet = Data.getPlanet();
			if (planet != null) {
				int f = (int) (Math.random() * 20d);
				int x = (int) (Math.random() * planet.getSize());
				int y = (int) (Math.random() * planet.getSize());

				Tile tile = planet.getFace(f).getTile(x,y);
				return new Variable(tile);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int require (variable)
		} else if (TokenConstants.REQUIRE.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				return new Variable(parameters[0].isNull() ? 0 : 1);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int setHeight (int height)
		} else if (TokenConstants.SET_HEIGHT.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				Tile tile = parameters[0].getTile();
				int height = parameters[1].getInt();

				if (tile != null) {
					if (height < 0) { height = 0; }
					if (height > TopologyConstants.PLANET_MAXIMUM_HEIGHT) { height = (int) TopologyConstants.PLANET_MAXIMUM_HEIGHT; }
					tile.setHeight(height);

					return new Variable(height);
				}
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int setWaterLevel (int level)
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

					return new Variable(level);
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
