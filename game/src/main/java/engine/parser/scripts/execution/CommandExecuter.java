package engine.parser.scripts.execution;

import constants.ResourcePathConstants;
import constants.ScriptConstants;
import constants.TopologyConstants;
import engine.data.Data;
import engine.data.Script;
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
import engine.parser.scripts.nodes.AbstractScriptNode;
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

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& effect addEffect (Instance target, Container effectContainer)
		} else if (TokenConstants.ADD_EFFECT.equals(command)) {
			if (parameters.length >= 4) {
				Instance target = parameters[0].getInstance();
				String name = parameters[1].getString();
				int duration = parameters[2].getInt();
				List<Variable> attributes = parameters[3].getList();

                checkValue(script, commandNode, target, "target instance");

				Effect effect = new Effect(-1);
				effect.setName(name);
				effect.setRemainingTicks(duration);
				effect.setSuperInstance(target);

				boolean getID = true;
				String attributeTextID = null;
				for (Variable variable : attributes) {
					if (getID) {
						if (variable.getType() == DataType.ATTRIBUTE) {
							effect.setAttribute(variable.getAttribute().getId(), variable.getAttribute().getValue());
						} else {
							attributeTextID = variable.getString();
							getID = false;
						}
					} else {
						int id = Data.getProtoAttributeID(attributeTextID);
						if (id >= 0) {
							effect.setAttribute(id, variable.getInt());
						} else {
							Logger.error("Attribute with textID '" + attributeTextID + "' does not exist!");
							// no strong exception handling here!
                            // the reason being, that we still try to extract the remaining attributes
						}
						getID = true;
					}
				}

				effect.run(ScriptConstants.EVENT_CREATE, new Variable[] {new Variable(target)});

				target.addEffect(effect);
			} else if (requireParameters(commandNode, 2)) {
				Instance target = parameters[0].getInstance();
				Container container = parameters[1].getContainer();
				int containerId = -1;

                checkValue(script, commandNode, target, "target instance");
                containerId = checkType(script, commandNode, container, parameters[1].getString());

				Effect effect = new Effect(containerId);
				effect.setSuperInstance(target);

				effect.run(ScriptConstants.EVENT_CREATE, new Variable[] {new Variable(target)});

				target.addEffect(effect);

				return new Variable(effect);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int addPersonalAtt (Instance target, String attributeTextID, int amount)
		} else if (TokenConstants.ADD_PERSONAL_ATTRIBUTE.equals(command)) {
			if (requireParameters(commandNode, 3)) {
				Instance target = parameters[0].getInstance();
				String attributeTextID = parameters[1].getString();
				int amount = parameters[2].getInt();
                int attributeID = Data.getProtoAttributeID(attributeTextID);

                checkValue(script, commandNode, target, "target instance");
                checkAttribute(script, commandNode, attributeID, attributeTextID);

				target.addAttribute(attributeID, amount);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void addOccupation (Instance instance, int duration[, String callBackScript])
		} else if (TokenConstants.ADD_OCCUPATION.equals(command)) {
			if (parameters.length >= 3) {
				Instance target = parameters[0].getInstance();
				int duration = parameters[1].getInt();
				Script callBackScript = parameters[2].getScript();

				if (callBackScript == null) {
					Container container = Data.getContainer(target.getId());
					if (container != null) {
						callBackScript = container.getScript(parameters[2].getString());
					}
				}

                checkValue(script, commandNode, target, "target instance");
                checkValue(script, commandNode, callBackScript, "callback script");

				target.addOccupation(duration, callBackScript);

			} else if (requireParameters(commandNode, 2)) {
                Instance target = parameters[0].getInstance();
                int duration = parameters[1].getInt();

                checkValue(script, commandNode, target, "target instance");

                target.addOccupation(duration, null);
            }

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& double chance (double probability)
		} else if (TokenConstants.CHANCE.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				double chance = parameters[0].getDouble();

				return new Variable(Math.random() < chance ? 1 : 0);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& Instance change (Instance target, Container / String container)
		} else if (TokenConstants.CHANGE.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				//Logger.error("The command '" + TokenConstants.CHANGE.getValue() + "' is not supported yet!");

				Instance target = parameters[0].getInstance();
				Container container = parameters[1].getContainer();
				int containerId = -1;

                checkValue(script, commandNode, target, "target instance");
                containerId = checkType(script, commandNode, container, parameters[1].getString());

				if (Data.getContainer(target.getId()).getType() == DataType.TILE && Data.getContainer(containerId).getType() == DataType.TILE) {
					target.setId(containerId);
					((Tile) target).resetColors();
					target.inheritAttributes();
					Data.addChangedTile((Tile) target);

					return new Variable(target);
				} else if (Data.getContainer(target.getId()).getType() != DataType.TILE && Data.getContainer(containerId).getType() != DataType.TILE) {
					target.setId(containerId);
					target.inheritAttributes();

					return new Variable(target);
				}
			}

        // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void changeSunAngle (double angle)
        } else if (TokenConstants.CHANGE_SUN_ANGLE.equals(command)) {
            if (requireParameters(commandNode, 1)) {
                double angle = parameters[0].getDouble();

                Data.getSun().changeAngle(angle);
            }

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance create (Container container, Instance holder)
		} else if (TokenConstants.CREATE.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				Container container = parameters[0].getContainer();
				Instance holder = parameters[1].getInstance();
				int containerId = -1;

				containerId = checkType(script, commandNode, container, parameters[0].getString());
                checkValue(script, commandNode, holder, "holder instance");

				Instance instance = new Instance(containerId);
				instance.placeInto(holder);

				instance.run(ScriptConstants.EVENT_PLACE, new Variable[] {parameters[1]});
				Data.addInstanceToQueue(instance);

				return new Variable(instance);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void deleteEffects (Instance target, Container effectContainer)
		} else if (TokenConstants.DELETE_EFFECTS.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				Instance target = parameters[0].getInstance();
				int containerID = parameters.length >= 2 ? parameters[1].getContainerId() : -1;

                checkValue(script, commandNode, target, "target instance");

				target.deleteEffects(containerID);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int destroy (Instance instance)
		} else if (TokenConstants.DESTROY.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				Instance instance = parameters[0].getInstance();

                checkValue(script, commandNode, instance, "target instance");

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

                checkValue(script, commandNode, instance, "target instance");
                checkAttribute(script, commandNode, attributeID, attributeTextID);

                return new Variable(instance.getAttribute(attributeID));
			} else if (requireParameters(commandNode, 1)) {
				String attributeTextID = parameters[0].getString();
				int attributeID = Data.getProtoAttributeID(attributeTextID);

                checkAttribute(script, commandNode, attributeID, attributeTextID);

                return new Variable(self.getAttribute(attributeID));
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance getAttInRange (String attributeTextID, Tile center, int radius)
		} else if (TokenConstants.GET_ATTRIBUTE_IN_RANGE.equals(command)) {
			if (requireParameters(commandNode, 3)) {
				String attributeTextID = parameters[0].getString();
				Tile center = parameters[1].getTile();
				int radius = parameters[2].getInt();

				int attributeID = Data.getProtoAttributeID(attributeTextID);

                checkValue(script, commandNode, center, "center tile");
                checkAttribute(script, commandNode, attributeID, attributeTextID);

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

                checkValue(script, commandNode, center, "center tile");
                containerID = checkType(script, commandNode, type, parameters[0].getString());

				TileArea tileArea = new TileArea(center, radius);
				for (Tile tile : tileArea.getTileList()) {
					Instance instance = tile.getThisOrSubInstanceWithID(containerID);

					if (instance != null) {
						return new Variable(instance);
					}
				}
				return new Variable();
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& List<Instance> getEffects (Instance target, Container effectContainer)
		} else if (TokenConstants.GET_EFFECTS.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				Instance target = parameters[0].getInstance();
				int containerID = parameters.length >= 2 ? parameters[1].getContainerId() : -1;

                checkValue(script, commandNode, target, "target instance");

				List<Variable> effects = new ArrayList<>();
				for (Instance effect : target.getEffects()) {
					if ((containerID == -1) || (containerID == effect.getId())) {
						effects.add(new Variable(effect));
					}
				}
				return new Variable(effects);
			}

        // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int getFullAtt (Instance instance, String attributeTextID)
        } else if (TokenConstants.GET_FULL_ATTRIBUTE.equals(command)) {
			if (parameters.length >= 2) {
				Instance instance = parameters[0].getInstance();

				String attributeTextID = parameters[1].getString();
				int attributeID = Data.getProtoAttributeID(attributeTextID);

				checkValue(script, commandNode, instance, "target instance");
				checkAttribute(script, commandNode, attributeID, attributeTextID);

				return new Variable(instance.getFullAttributeValue(attributeID));
			} else if (requireParameters(commandNode, 1)) {
				String attributeTextID = parameters[0].getString();
				int attributeID = Data.getProtoAttributeID(attributeTextID);

				checkAttribute(script, commandNode, attributeID, attributeTextID);

				return new Variable(self.getFullAttributeValue(attributeID));
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& Instance getHolder(Instance target)
		} else if (TokenConstants.GET_HOLDER.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				Instance target = parameters[0].getInstance();

				checkValue(script, commandNode, target, "target instance");

				return new Variable(target.getSuperInstance());
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int getHeight (Tile tile)
		} else if (TokenConstants.GET_HEIGHT.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				Tile tile = parameters[0].getTile();

                checkValue(script, commandNode, tile, "target tile");

				return new Variable(tile.getHeight());
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance[] getInstancesInRange (Container type, Tile center, int radius)
		} else if (TokenConstants.GET_INSTANCES_IN_RANGE.equals(command)) {
			if (requireParameters(commandNode, 3)) {
				Container type = parameters[0].getContainer();
				int containerID = -1;
				Tile center = parameters[1].getTile();
				int radius = parameters[2].getInt();

				checkValue(script, commandNode, center, "center tile");
				containerID = checkType(script, commandNode, type, parameters[0].getString());

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

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance getItemAtt (Instance holder, String attributeTextID)
		} else if (TokenConstants.GET_ITEM_ATTRIBUTE.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				Instance holder = parameters[0].getInstance();
				String attributeTextID = parameters[1].getString();
				int attributeID = Data.getProtoAttributeID(attributeTextID);

                checkValue(script, commandNode, holder, "holder instance");
                checkAttribute(script, commandNode, attributeID, attributeTextID);

				if (holder.getSubInstances() != null) {
					for (Instance item : holder.getSubInstances()) {
						if (item != null) {
							if (item.getAttributeValue(attributeID) > 0) {
								return new Variable(item);
							}
						}
					}
				}
				return new Variable();
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& List<instance> getItems (Instance holder [, Container type])
		} else if (TokenConstants.GET_ITEMS.equals(command)) {
			int containerID = -1;

			if (parameters.length >= 2) {
				Container type = parameters[1].getContainer();
				containerID = checkType(script, commandNode, type, parameters[1].getString());
			}

			if (requireParameters(commandNode, 1)) {
				Instance holder = parameters[0].getInstance();

                checkValue(script, commandNode, holder, "holder instance");

				if (holder.getSubInstances() != null) {
					List<Variable> items = new ArrayList<>(holder.getSubInstances().size());
					for (Instance sub : holder.getSubInstances()) {
						if (sub != null && (containerID == -1 || containerID == sub.getId())) {
							items.add(new Variable(sub));
						}
					}
					return new Variable(items);
				}
				return new Variable();
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& double getLightLevel (Tile tile)
		} else if (TokenConstants.GET_LIGHT_LEVEL.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				Tile tile = parameters[0].getTile();

                checkValue(script, commandNode, tile, "target tile");

				Vector3 sunPosition = Data.getSun().getGraphicalObject().getPosition().normalize();
				Vector3 tilePosition = tile.getTileMesh().getNormal();

				double dotProduct = sunPosition.dot(tilePosition); // ranges from -1 to 1

				return new Variable(50d * (dotProduct + 1d)); // ranges from 0 to 100
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& tile getNeighbor (Tile tile, int position)
		} else if (TokenConstants.GET_NEIGHBOUR.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				Tile tile = parameters[0].getTile();
				int position = parameters[1].getInt();

                checkValue(script, commandNode, tile, "target tile");

				if (position < 0 || position > 2) {
					position = position % 3;
				}

				return new Variable(Neighbour.getNeighbour(tile, position));
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& list<tile> getNeighbors (Tile tile)
		} else if (TokenConstants.GET_NEIGHBOURS.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				Tile tile = parameters[0].getTile();

                checkValue(script, commandNode, tile, "target tile");

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

                checkValue(script, commandNode, instance, "target instance");

				return new Variable(instance.getPosition());
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& list<tile> getTilesInRange (Tile tile, int radius)
		} else if (TokenConstants.GET_TILES_IN_RANGE.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				Tile center = parameters[0].getTile();
				int radius = parameters[1].getInt();

                checkValue(script, commandNode, center, "center tile");

				TileArea tileArea = new TileArea(center, radius);
				return new Variable(tileArea.getTilesAsVariableList());
			}

        // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& Container getType (Instance instance)
        } else if (TokenConstants.GET_TYPE.equals(command)) {
            if (requireParameters(commandNode,1)) {
                Instance instance = parameters[0].getInstance();

                checkValue(script, commandNode, instance, "target instance");

                return new Variable(Data.getContainer(instance.getId()));
            }

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int getWaterHeight (Tile tile)
		} else if (TokenConstants.GET_WATER_HEIGHT.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				Tile tile = parameters[0].getTile();

				checkValue(script, commandNode, tile, "target tile");

				return new Variable(tile.getWaterHeight());
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& boolean isOnFloor(Instance instance)
		} else if (TokenConstants.IS_ON_FLOOR.equals(command)) {
			if (requireParameters(commandNode,1)) {
				Instance instance = parameters[0].getInstance();

				checkValue(script, commandNode, instance, "target instance");

				if (instance.getSuperInstance() != null) {
					Container superContainer = Data.getContainer(instance.getSuperInstance().getId());
					if (superContainer != null && superContainer.getType() == DataType.TILE) {
						return new Variable(1);
					}
				}

				return new Variable(0);
			}

        // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int length (List list)
        } else if (TokenConstants.LENGTH.equals(command)) {
            if (requireParameters(commandNode,1)) {
                List<Variable> list = parameters[0].getList();

                checkValue(script, commandNode, list, "list");

                return new Variable(list.size());
            }

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& double max (double d1, double d2, ...)
		} else if (TokenConstants.MAX.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				double max = 0;
				boolean first = true;

				for (Variable variable : parameters) {
					if (variable.getType() == DataType.LIST) {
						for (Variable sub : variable.getList()) {
							if (first || sub.getDouble() > max) {
								max = sub.getDouble();
								first = false;
							}
						}
					} else {
						if (first || variable.getDouble() > max) {
							max = variable.getDouble();
							first = false;
						}
					}
				}

				return new Variable(max);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& double min (double d1, double d2, ...)
		} else if (TokenConstants.MIN.equals(command)) {
			if (requireParameters(commandNode, 1)) {
				double min = 0;
				boolean first = true;

				for (Variable variable : parameters) {
					if (variable.getType() == DataType.LIST) {
						for (Variable sub : variable.getList()) {
							if (first || sub.getDouble() < min) {
								min = sub.getDouble();
								first = false;
							}
						}
					} else {
						if (first || variable.getDouble() < min) {
							min = variable.getDouble();
							first = false;
						}
					}
				}

				return new Variable(min);
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void mixAttributes (Instance t, Instance p1, Instance p2)
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

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& tile moveTo (Instance instance, Tile tile, int steps)
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
				return new Variable();
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance pickUp (Instance holder, Instance item)
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

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& Tile setAt (Instance instance, Tile tile)
		/*} else if (TokenConstants.SET_AT.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				Instance instance = parameters[0].getInstance();
				Tile tile = parameters[1].getTile();

                checkValue(script, commandNode, tile, "target tile");
                checkValue(script, commandNode, instance, "target instance");

				instance.placeInto(tile);
				return new Variable(tile);
			}*/

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

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void setMesh (Instance target, String path)
		} else if (TokenConstants.SET_MESH.equals(command)) {
			if (requireParameters(commandNode, 2)) {
				Instance target = parameters[0].getInstance();
				String path = parameters[1].getString();

				checkValue(script, commandNode, target, "target instance");

				target.setMesh(ResourcePathConstants.MOD_FOLDER + path);

				return new Variable("Semira <3");
			}

        // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& string setSunAngle (double angle)
        } else if (TokenConstants.SET_SUN_ANGLE.equals(command)) {
            if (requireParameters(commandNode, 1)) {
                double angle = parameters[0].getDouble();

                Data.getSun().setAngle(angle);

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

        // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& UNKNOWN COMMAND!
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
