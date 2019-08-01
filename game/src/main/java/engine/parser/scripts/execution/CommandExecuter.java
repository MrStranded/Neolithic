package engine.parser.scripts.execution;

import constants.ScriptConstants;
import constants.TopologyConstants;
import engine.data.Data;
import engine.data.Script;
import engine.data.behaviour.Occupation;
import engine.data.entities.Instance;
import engine.data.planetary.Face;
import engine.data.planetary.Planet;
import engine.data.planetary.Tile;
import engine.data.proto.Container;
import engine.data.variables.Variable;
import engine.logic.topology.Neighbour;
import engine.logic.topology.Pathfinding;
import engine.logic.topology.TopologyGenerator;
import engine.logic.topology.TileArea;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.exceptions.ReturnException;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.nodes.CommandExpressionNode;
import engine.parser.tokenization.Token;
import engine.parser.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class CommandExecuter {

	public static Variable executeCommand(Instance self, Script script, CommandExpressionNode commandNode) throws ScriptInterruptedException {
		//System.out.println("execute command: " + commandNode.getCommand());

		Token command = commandNode.getCommand();
		Variable[] parameters = ParameterCalculator.calculateParameters(self, script, commandNode);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& double abs (double value)
		if (TokenConstants.ABSOLUTE.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				double value = parameters[0].getDouble();
				return new Variable(Math.abs(value));
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int addPersonalAtt (Instance target, String attributeTextID, int amount)
		} else if (TokenConstants.ADD_PERSONAL_ATTRIBUTE.equals(command)) {
			if (requireParameters(commandNode, 3)) {
				Instance target = parameters[0].getInstance();
				String attributeTextID = parameters[1].getString();
				int amount = parameters[2].getInt();

				if (target == null) {
					Logger.error("Target instance value for command '" + command.getValue() + "' is invalid!");
					return new Variable();
				}

				int attributeID = Data.getProtoAttributeID(attributeTextID);
				if (attributeID == -1) {
					Logger.error("Attribute '" + attributeTextID + "' does not exist!");
					return new Variable();
				}

				target.addAttribute(attributeID, amount);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void addOccupation (Instance instance, int duration[, String callBackScript])
		} else if (TokenConstants.ADD_OCCUPATION.equals(command)) {
			if (parameters.length >= 3) {
				Instance target = parameters[0].getInstance();
				int duration = parameters[1].getInt();
				String callBackScript = parameters[2].getString();

				if (target == null) {
					Logger.error("Target instance value for command '" + command.getValue() + "' is invalid!");
					return new Variable();
				}

				target.addOccupation(duration, callBackScript);

			} else if (requireParameters(commandNode, 2)) {
                Instance target = parameters[0].getInstance();
                int duration = parameters[1].getInt();

                if (target == null) {
                    Logger.error("Target instance value for command '" + command.getValue() + "' is invalid!");
                    return new Variable();
                }

                target.addOccupation(duration, null);
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
					Logger.error("Instance value for command '" + command.getValue() + "' is invalid!");
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
						Logger.error("Instance value for command '" + command.getValue() + "' is invalid!");
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
					Logger.error("Tile value for command '" + command.getValue() + "' is invalid on line " + command.getLine());
					return new Variable();
				}

				if (attributeID == -1) {
					Logger.error("Attribute '" + attributeTextID + "' does not exist!");
					return new Variable();
				}

				TileArea tileArea = new TileArea(center, radius);
				for (Tile tile : tileArea.getTileList()) {
					Instance instance = tile.getSubInstanceWithAttribute(attributeID);

					if (instance != null) {
						return new Variable(instance);
					}
				}
				return new Variable();
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance getCreatureInRange (Container type, Tile center, int radius)
		} else if (TokenConstants.GET_CREATURE_IN_RANGE.equals(command)) {
			if (requireParameters(commandNode, 3)) {
				Container type = parameters[0].getContainer();
				int containerID = -1;
				Tile center = parameters[1].getTile();
				int radius = parameters[2].getInt();

				if (center == null) {
					Logger.error("Tile value for command '" + command.getValue() + "' is invalid on line " + command.getLine());
					return new Variable();
				}

				if ((type == null) || ((containerID = Data.getContainerID(type.getTextID())) < 0)) {
					Logger.error("Type of value '" + parameters[0].toString() + "' does not exist!");
					return new Variable();
				}

				TileArea tileArea = new TileArea(center, radius);
				for (Tile tile : tileArea.getTileList()) {
					Instance instance = tile.getThisOrSubInstanceWithID(containerID);

					if (instance != null) {
						return new Variable(instance);
					}
				}
				return new Variable();
			}

        // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance[] getCreaturesInRange (Container type, Tile center, int radius)
        } else if (TokenConstants.GET_CREATURES_IN_RANGE.equals(command)) {
            if (requireParameters(commandNode, 3)) {
                Container type = parameters[0].getContainer();
                int containerID = -1;
                Tile center = parameters[1].getTile();
                int radius = parameters[2].getInt();

                if (center == null) {
                    Logger.error("Tile value for command '" + command.getValue() + "' is invalid on line " + command.getLine());
                    return new Variable();
                }

                if ((type == null) || ((containerID = Data.getContainerID(type.getTextID())) < 0)) {
                    Logger.error("Type of value '" + parameters[0].toString() + "' does not exist!");
                    return new Variable();
                }

                List<Variable> creatures = new ArrayList<>();
                TileArea tileArea = new TileArea(center, radius);
                for (Tile tile : tileArea.getTileList()) {
                    Instance instance = tile.getThisOrSubInstanceWithID(containerID);

                    if (instance != null) {
                        creatures.add(new Variable(instance));
                    }
                }
                return new Variable(creatures);
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
					Logger.error("Holder instance value for command '" + command.getValue() + "' is invalid on line " + command.getLine());
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
					Logger.error("Tile value for command '" + command.getValue() + "' is invalid on line " + command.getLine());
					return new Variable();
				}
				if (position < 0 || position > 2) {
					position = position % 3;
				}

				return new Variable(Neighbour.getNeighbour(tile, position));
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& list<tile> getNeighbors (Tile tile)
		} else if (TokenConstants.GET_NEIGHBOURS.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				Tile tile = parameters[0].getTile();

				if (tile == null) {
					Logger.error("Tile value for command '" + command.getValue() + "' is invalid on line " + command.getLine());
					return new Variable();
				}

				List<Variable> tileList = new ArrayList<>(3);
				for (Tile neighbour : Neighbour.getNeighbours(tile)) {
					tileList.add(new Variable(neighbour));
				}
				return new Variable(tileList);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& tile getTile (Instance instance)
		} else if (TokenConstants.GET_TILE.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				Instance instance = parameters[0].getInstance();

				if (instance == null) {
					Logger.error("Instance value for command '" + command.getValue() + "' is invalid on line " + command.getLine());
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
					Logger.error("Tile value for command '" + command.getValue() + "' is invalid on line " + command.getLine());
					return new Variable();
				}

				TileArea tileArea = new TileArea(center, radius);
				return new Variable(tileArea.getTilesAsVariableList());
			}

        // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& Container getType (Instance instance)
        } else if (TokenConstants.GET_TYPE.equals(command)) {
            if (requireParameters(commandNode,1)) {
                Instance instance = parameters[0].getInstance();

                if (instance == null) {
                    Logger.error("Instance for command '" + command.getValue() + "' does not exist on line " + command.getLine());
                    return new Variable();
                }

                return new Variable(Data.getContainer(instance.getId()));
            }

        // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int length (List list)
        } else if (TokenConstants.LENGTH.equals(command)) {
            if (requireParameters(commandNode,1)) {
                List<Variable> list = parameters[0].getList();

                if (list == null) {
                    Logger.error("Value for command '" + command.getValue() + "' is not a list on line " + command.getLine());
                    return new Variable();
                }

                return new Variable(list.size());
            }

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void mixAttributes (Instance t, Instance p1, Instance p2, double chance, double extent)
		} else if (TokenConstants.MIX_ATTRIBUTES.equals(command)) {
			if (requireParameters(commandNode, 5)) {
				Instance target = parameters[0].getInstance();
				Instance parent1 = parameters[1].getInstance();
				Instance parent2 = parameters[2].getInstance();
				double chance = parameters[3].getDouble();
				double extent = parameters[4].getDouble();

                if (target == null) {
                    Logger.error("Target instance value for command '" + command.getValue() + "' is invalid on line " + command.getLine());
                    return new Variable();
                }
                if (parent1 == null) {
                    Logger.error("Parent1 instance value for command '" + command.getValue() + "' is invalid on line " + command.getLine());
                    return new Variable();
                }
                if (parent2 == null) {
                    Logger.error("Parent2 instance value for command '" + command.getValue() + "' is invalid on line " + command.getLine());
                    return new Variable();
                }

                List<Integer> attributes = Data.getAllAttributeIDs();
                for (Integer id : attributes) {
                    int value = (parent1.getPersonalAttributeValue(id) + parent2.getPersonalAttributeValue(id)) / 2;

                    if (Math.random() <= chance/100d) {
                        value += -extent + Math.random()*extent;
                    }

                    target.setAttribute(id, value);
                }
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& tile moveTo (Instance instance, Tile tile, int steps)
		} else if (TokenConstants.MOVE_TO.equals(command)) {
			if (requireParameters(commandNode, 3)) {
				Instance instance = parameters[0].getInstance();
				Tile tile = parameters[1].getTile();
				int steps = parameters[2].getInt();

				if (tile == null) {
					Logger.error("Tile value for command '" + command.getValue() + "' is invalid on line " + command.getLine());
					return new Variable();
				}
				if (instance == null) {
					Logger.error("Instance value for command '" + command.getValue() + "' is invalid on line " + command.getLine());
					return new Variable();
				}

				Tile newPosition = Pathfinding.moveTowardsTile(instance, tile, steps);

				if (newPosition != instance.getPosition()) {
					instance.placeInto(newPosition);
				}
				return new Variable();
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance pickUp (Instance holder, Instance item)
		} else if (TokenConstants.PICK_UP.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				Instance holder = parameters[0].getInstance();
				Instance item = parameters[1].getInstance();

				if (holder == null) {
					Logger.error("Holder instance value for command '" + command.getValue() + "' is invalid on line " + command.getLine());
					return new Variable();
				}
				if (item == null) {
					Logger.error("Item instance value for command '" + command.getValue() + "' is invalid on line " + command.getLine());
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
				throw new ReturnException(parameters[0]);
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

        // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& string setAt (Instance instance, Tile tile)
        } else if (TokenConstants.SET_AT.equals(command)) {
            if (requireParameters(commandNode, 2)) {
                Instance instance = parameters[0].getInstance();
                Tile tile = parameters[1].getTile();

                if (tile == null) {
                    Logger.error("Tile value for command '" + command.getValue() + "' is invalid on line " + command.getLine());
                    return new Variable();
                }
                if (instance == null) {
                    Logger.error("Instance value for command '" + command.getValue() + "' is invalid on line " + command.getLine());
                    return new Variable();
                }

                instance.placeInto(tile);
                return new Variable("Semira <3");
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
