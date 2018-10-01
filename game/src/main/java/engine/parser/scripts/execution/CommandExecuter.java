package engine.parser.scripts.execution;

import constants.ScriptConstants;
import constants.TopologyConstants;
import engine.data.Data;
import engine.data.Script;
import engine.data.entities.Instance;
import engine.data.planetary.Face;
import engine.data.planetary.Planet;
import engine.data.planetary.Tile;
import engine.data.proto.Container;
import engine.data.variables.Variable;
import engine.logic.tiles.Neighbour;
import engine.logic.TopologyGenerator;
import engine.logic.tiles.TileArea;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.nodes.CommandExpressionNode;
import engine.parser.tokenization.Token;
import engine.parser.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class CommandExecuter {

	public static Variable executeCommand(Instance self, Script script, CommandExpressionNode commandNode) {
		//System.out.println("execute command: " + commandNode.getCommand());

		Token command = commandNode.getCommand();
		Variable[] parameters = ParameterCalculator.calculateParameters(self, script, commandNode);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int addPersonalAtt (Instance target, String attributeTextID, int amount)
		if (TokenConstants.ADD_PERSONAL_ATTRIBUTE.equals(command)) {
			if (requireParameters(commandNode, 3)) {
				Instance target = parameters[0].getInstance();
				String attributeTextID = parameters[1].getString();
				int amount = parameters[2].getInt();

				if (target == null) {
					Logger.error("Target instance value for command '" + TokenConstants.ADD_PERSONAL_ATTRIBUTE.getValue() + "' is invalid!");
					return new Variable();
				}

				int attributeID = Data.getProtoAttributeID(attributeTextID);
				if (attributeID == -1) {
					Logger.error("Attribute '" + attributeTextID + "' does not exist!");
					return new Variable();
				}

				target.addAttribute(attributeID, amount);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& double chance (double probability)
		} else if (TokenConstants.CHANCE.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				double chance = parameters[0].getDouble();

				return new Variable(Math.random() < chance ? 1 : 0);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance create (Container container, Tile tile)
		} else if (TokenConstants.CREATE.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				Container container = parameters[0].getContainer();
				Tile tile = parameters[1].getTile();

				if (container != null && tile != null) {
					int id = Data.getContainerID(container.getTextID());
					Instance instance = new Instance(id);
					instance.placeInto(tile);
					Variable[] newParameters = new Variable[1];
					newParameters[0] = parameters[1];

					instance.run(ScriptConstants.EVENT_PLACE, newParameters);
					Data.addInstanceToQueue(instance);

					return new Variable(instance);
				} else {
					if (container == null) {
						Logger.error("Cannot create Instance: Template for '" + parameters[0] + "' does not exist. Line " + command.getLine());
					}
					if (tile == null) {
						Logger.error("Cannot create Instance: Not a valid tile value.");
					}
				}
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int destroy (Instance instance)
		} else if (TokenConstants.DESTROY.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				Instance instance = parameters[0].getInstance();

				if (instance == null) {
					Logger.error("Instance value for command '" + TokenConstants.DESTROY.getValue() + "' is invalid!");
					return new Variable();
				}

				instance.destroy();
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& list eachTile ()
		} else if (TokenConstants.EACH_TILE.equals(command)) {
			Planet planet = Data.getPlanet();
			if (planet != null) {
				List<Variable> tileList = new ArrayList<>(planet.getSize() * planet.getSize() * 20);
				for (Face face : planet.getFaces()) {
					for (Tile tile : face.getTiles()) {
						tileList.add(new Variable(tile));
					}
				}
				return new Variable(tileList);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int fitTiles ()
		} else if (TokenConstants.FIT_TILES.equals(command)) {
			if (Data.getPlanet() != null) {
				TopologyGenerator.fitTiles(Data.getPlanet());
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int getAttributeValue ([Instance instance,] String attribute)
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

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance getAttInRange (String attributeTextID, Tile center, int radius)
		} else if (TokenConstants.GET_ATTRIBUTE_IN_RANGE.equals(command)) {
			if (requireParameters(commandNode, 3)) {
				String attributeTextID = parameters[0].getString();
				Tile center = parameters[1].getTile();
				int radius = parameters[2].getInt();

				int attributeID = Data.getProtoAttributeID(attributeTextID);

				if (center == null) {
					Logger.error("Tile value for command '" + TokenConstants.GET_ATTRIBUTE_IN_RANGE.getValue() + "' is invalid on line " + command.getLine());
					return new Variable();
				}

				if (attributeID == -1) {
					Logger.error("Attribute '" + attributeTextID + "' does not exist!");
					return new Variable();
				}

				TileArea tileArea = new TileArea(center, radius);
				for (Tile tile : tileArea.getTileList()) {
					for (Instance instance : tile.getSubInstances()) {
						if (instance != null) {
							if (instance.getAttributeValue(attributeID) > 0) {
								return new Variable(instance);
							}
						}
					}
				}
				return new Variable();
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int getHeight (Tile tile)
		} else if (TokenConstants.GET_HEIGHT.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				Tile tile = parameters[0].getTile();

				if (tile == null) {
					return new Variable();
				}

				return new Variable(tile.getHeight());
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance getItemAtt (Instance holder, String attributeTextID)
		} else if (TokenConstants.GET_ITEM_ATTRIBUTE.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				Instance holder = parameters[0].getInstance();
				String attributeTextID = parameters[1].getString();
				int attributeID = Data.getProtoAttributeID(attributeTextID);

				if (holder == null) {
					Logger.error("Holder instance value for command '" + TokenConstants.GET_ITEM_ATTRIBUTE.getValue() + "' is invalid on line " + command.getLine());
					return new Variable();
				}
				if (attributeID == -1) {
					Logger.error("Attribute '" + attributeTextID + "' does not exist!");
					return new Variable();
				}

				for (Instance item : holder.getSubInstances()) {
					if (item != null) {
						if (item.getAttributeValue(attributeID) > 0) {
							return new Variable(item);
						}
					}
				}
				return new Variable();
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

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& list<tile> getTilesInRange (Tile tile, int radius)
		} else if (TokenConstants.GET_TILES_IN_RANGE.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				Tile center = parameters[0].getTile();
				int radius = parameters[1].getInt();

				if (center == null) {
					Logger.error("Tile value for command '" + TokenConstants.GET_TILES_IN_RANGE.getValue() + "' is invalid on line " + command.getLine());
					return new Variable();
				}

				TileArea tileArea = new TileArea(center, radius);
				return new Variable(tileArea.getTilesAsVariableList());
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& string moveTo (Instance instance, Tile tile)
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

				instance.placeInto(tile);
				return new Variable("Semira <3");
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance pickUp (Instance holder, Instance item)
		} else if (TokenConstants.PICK_UP.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				Instance holder = parameters[0].getInstance();
				Instance item = parameters[1].getInstance();

				if (holder == null) {
					Logger.error("Holder instance value for command '" + TokenConstants.PICK_UP.getValue() + "' is invalid on line " + command.getLine());
					return new Variable();
				}
				if (item == null) {
					Logger.error("Item instance value for command '" + TokenConstants.PICK_UP.getValue() + "' is invalid on line " + command.getLine());
					return new Variable();
				}

				//item.placeInto(holder.getPosition());
				//holder.addSubInstance(item);
				item.placeInto(holder);
				return new Variable(item);
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

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& variable return (variable)
		} else if (TokenConstants.RETURN.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				return parameters[0];
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
