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

		Token command = commandNode.getCommand();
		String commandName = commandNode.getCommand().getValue();
		Variable[] parameters = ParameterCalculator.calculateParameters(self, script, commandNode);

		Command c = commands.get(commandName);

		if (c != null) {
			return c.execute(self, parameters);
		}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&& boolean isSelected(Instance instance)
		if (TokenConstants.IS_SELECTED.equals(command)) {
			if (requireParameters(commandNode,1)) {
				Instance instance = parameters[0].getInstance();

				checkValue(script, commandNode, instance, "target instance");

				return new Variable(instance == GameOptions.selectedInstance);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&& void mixAttributes (Instance t, Instance p1, Instance p2)
		} else if (TokenConstants.MIX_ATTRIBUTES.equals(command)) {
			if (requireParameters(commandNode, 3)) {
				Instance target = parameters[0].getInstance();
				Instance parent1 = parameters[1].getInstance();
				Instance parent2 = parameters[2].getInstance();

                checkValue(script, commandNode, target, "child instance");
                checkValue(script, commandNode, parent1, "parent 1 instance");
                checkValue(script, commandNode, parent2, "parent 2 instance");

                List<Integer> attributes = Data.getAllAttributeIDs();
                for (Integer id : attributes) {
					ProtoAttribute protoAttribute = Data.getProtoAttribute(id);

                	if (protoAttribute != null && protoAttribute.isInherited()) {
                		double v1 = parent1.getPersonalAttributeValue(id);
                		double v2 = parent2.getPersonalAttributeValue(id);
                		if (v1 != 0 || v2 != 0) {
                			double p = Math.random();
							int value = (int) (v1*p + v2*(1-p));

							if (Math.random() < protoAttribute.getMutationChance() / 100d) {
								// +1 because (int) rounds the result down. Math.random() is always < 1
								value += Math.floor(-protoAttribute.getMutationExtent() + Math.random() * (2d*protoAttribute.getMutationExtent() + 1d));
							}

							target.setAttribute(id, value);
						}
					}
                }
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&& tile moveTo (Instance instance, Tile tile, int steps)
		} else if (TokenConstants.MOVE_TO.equals(command)) {
			if (requireParameters(commandNode, 3)) {
				Instance instance = parameters[0].getInstance();
				Tile tile = parameters[1].getTile();
				int steps = parameters[2].getInt();

                checkValue(script, commandNode, tile, "target tile");
                checkValue(script, commandNode, instance, "target instance");

				Tile newPosition = Pathfinding.moveTowardsTile(instance, tile, steps);

				if (newPosition != instance.getPosition()) {
					instance.placeInto(newPosition);
				}
				return new Variable(newPosition);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&& instance pickUp (Instance holder, Instance item)
		} else if (TokenConstants.PICK_UP.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				Instance holder = parameters[0].getInstance();
				Instance item = parameters[1].getInstance();

                checkValue(script, commandNode, holder, "holder instance");
                checkValue(script, commandNode, item, "item instance");

				//item.placeInto(holder.getPosition());
				//holder.addSubInstance(item);
				item.placeInto(holder);
				return new Variable(item);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&& string print (String text)
		} else if (TokenConstants.PRINT.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				String text = parameters[0].getString();
				Logger.log("PRINT: " + text);
				return new Variable(text);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&& tile randomTile ()
		} else if (TokenConstants.RANDOM_TILE.equals(command)) {
			Planet planet = Data.getPlanet();
			if (planet != null) {
				int f = (int) (Math.random() * 20d);
				int x = (int) (Math.random() * planet.getSize());
				int y = (int) (Math.random() * planet.getSize());

				Tile tile = planet.getFace(f).getTile(x,y);
				return new Variable(tile);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&& variable return (variable)
		} else if (TokenConstants.RETURN.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				throw new ReturnException(parameters[0]);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&& int require (variable)
		} else if (TokenConstants.REQUIRE.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				boolean fulfilled = !parameters[0].isNull();
				if (!fulfilled) {
					throw new ReturnException(new Variable(0));
				}
				return new Variable(1);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&& Tile setAt (Instance instance, Tile tile)
		/*} else if (TokenConstants.SET_AT.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				Instance instance = parameters[0].getInstance();
				Tile tile = parameters[1].getTile();

                checkValue(script, commandNode, tile, "target tile");
                checkValue(script, commandNode, instance, "target instance");

				instance.placeInto(tile);
				return new Variable(tile);
			}*/

		// &&&&&&&&&&&&&&&&&&&&&&&&&&& int setHeight (int height)
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

		// &&&&&&&&&&&&&&&&&&&&&&&&&&& void setMesh (Instance target, String path)
		} else if (TokenConstants.SET_MESH.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				Instance target = parameters[0].getInstance();
				String path = parameters[1].getString();

				checkValue(script, commandNode, target, "target instance");

				target.setMesh(ResourcePathConstants.MOD_FOLDER + path);

				return new Variable("Semira <3");
			}

        // &&&&&&&&&&&&&&&&&&&&&&&&&&& string setSunAngle (double angle)
        } else if (TokenConstants.SET_SUN_ANGLE.equals(command)) {
            if (requireParameters(commandNode, 1)) {
                double angle = parameters[0].getDouble();

                Data.getSun().setAngle(angle);

                return new Variable("Semira <3");
            }

		// &&&&&&&&&&&&&&&&&&&&&&&&&&& int setWaterHeight (int height)
		} else if (TokenConstants.SET_WATER_HEIGHT.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				Tile tile = parameters[0].getTile();
				int height = parameters[1].getInt();

				if (tile != null) {
					if (height < 0) { height = 0; }
					if (height > TopologyConstants.PLANET_MAXIMUM_HEIGHT) { height = (int) TopologyConstants.PLANET_MAXIMUM_HEIGHT; }
					tile.setWaterHeight(height);

					return new Variable(height);
				}
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&& int setWaterLevel (int level)
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

		// &&&&&&&&&&&&&&&&&&&&&&&&&&& void updatePlanetMesh ()
		} else if (TokenConstants.UPDATE_PLANET_MESH.equals(command)) {
			Data.setUpdatePlanetMesh(true);

        // &&&&&&&&&&&&&&&&&&&&&&&&&&& UNKNOWN COMMAND!
		} else {
			Logger.error(
					"Unknown command '" + commandNode.getCommand() + "'!" +
					" Error on line " + commandNode.getCommand().getLine() +
					" in command '" + commandNode.getCommand().getValue() + "'" +
					" during execution of script '" + script.getTextId() + "' in file '" + script.getFileName() + "'."
			);

        }

		return new Variable();
	}

	private static void checkValue(Script script, CommandExpressionNode commandNode, Object value, String objectName) throws InvalidValueException {
        if (value == null) {
            Logger.error(
                    "Value '" + objectName + "' is invalid!" +
                    " Error on line " + commandNode.getCommand().getLine() +
                    " in command '" + commandNode.getCommand().getValue() + "'" +
					" during execution of script '" + script.getTextId() + "' in file '" + script.getFileName() + "'."
            );
            throw new InvalidValueException("Value '" + objectName + "' is invalid!");
        }
    }

    private static void checkAttribute(Script script, CommandExpressionNode commandNode, int attributeID, String attributeTextID) throws InvalidValueException {
        if (attributeID == -1) {
            Logger.error(
                    "Attribute '" + attributeTextID + "' does not exist!" +
                    " Error on line " + commandNode.getCommand().getLine() +
                    " in command '" + commandNode.getCommand().getValue() + "'" +
					" during execution of script '" + script.getTextId() + "' in file '" + script.getFileName() + "'."
            );
            throw new InvalidValueException("Attribute '" + attributeTextID + "' does not exist!");
        }
    }

    private static int checkType(Script script, CommandExpressionNode commandNode, Container type, String typeName) throws InvalidValueException {
	    int containerID = -1;

        if ((type == null) || ((containerID = Data.getContainerID(type.getTextID())) < 0)) {
            Logger.error(
                    "Type with name '" + typeName + "' does not exist!" +
                    " Error on line " + commandNode.getCommand().getLine() +
                    " in command '" + commandNode.getCommand().getValue() + "'" +
					" during execution of script '" + script.getTextId() + "' in file '" + script.getFileName() + "'."
            );
            throw new InvalidValueException("Type with name '" + typeName + "' does not exist!");
        }

        return containerID;
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
