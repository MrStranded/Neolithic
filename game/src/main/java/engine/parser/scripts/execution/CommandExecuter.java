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

		TokenConstants commandToken = TokenConstants.getCorrespondingConstant(command);
		if (commandToken == null) { return new Variable(); }

		switch (commandToken) {
			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& double abs (double value)
			case ABSOLUTE:
				if (requireParameters(commandNode, 1)) {
					double value = parameters[0].getDouble();
					return new Variable(Math.abs(value));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& effect addEffect (Instance target, Container effectContainer)
			case ADD_EFFECT:
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
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int addPersonalAtt (Instance target, String attributeTextID, int amount)
			case ADD_PERSONAL_ATTRIBUTE:
				if (requireParameters(commandNode, 3)) {
					Instance target = parameters[0].getInstance();
					String attributeTextID = parameters[1].getString();
					int amount = parameters[2].getInt();
					int attributeID = Data.getProtoAttributeID(attributeTextID);

					checkValue(script, commandNode, target, "target instance");
					checkAttribute(script, commandNode, attributeID, attributeTextID);

					target.addAttribute(attributeID, amount);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void addOccupation (Instance instance, int duration[, String callBackScript])
			case ADD_OCCUPATION:
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
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int ceil ()
			case CEIL:
				if (requireParameters(commandNode, 1)) {
					return new Variable(Math.ceil(parameters[0].getDouble()));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& double chance (double probability)
			case CHANCE:
				if (requireParameters(commandNode, 1)) {
					double chance = parameters[0].getDouble();

					return new Variable(Math.random() < chance ? 1 : 0);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& Instance change (Instance target, Container / String container)
			case CHANGE:
				if (requireParameters(commandNode, 2)) {
					//Logger.error("The command '" + TokenConstants.CHANGE.getValue() + "' is not supported yet!");

					Instance target = parameters[0].getInstance();
					Container container = parameters[1].getContainer();
					int containerId = -1;

					checkValue(script, commandNode, target, "target instance");
					containerId = checkType(script, commandNode, container, parameters[1].getString());

					target.change(containerId);

					return new Variable(target);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void changeSunAngle (double angle)
			case CHANGE_SUN_ANGLE:
				if (requireParameters(commandNode, 1)) {
					double angle = parameters[0].getDouble();

					Data.getSun().changeAngle(angle);

					return new Variable(0);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& boolean contains (List list, Variable element)
			case CONTAINS:
				if (requireParameters(commandNode, 2)) {
					List<Variable> list = parameters[0].getList();
					Variable element = parameters[1];

					checkValue(script, commandNode, list, "list");
					checkValue(script, commandNode, element, "search element");

					for (Variable variable : list) {
						if (variable != null && variable.equals(element)) {
							return new Variable(1);
						}
					}

					return new Variable(0);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance create (Container container, Instance holder)
			case CREATE:
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
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void deleteEffects (Instance target, Container effectContainer)
			case DELETE_EFFECTS:
				if (requireParameters(commandNode, 1)) {
					Instance target = parameters[0].getInstance();
					int containerID = parameters.length >= 2 ? parameters[1].getContainerId() : -1;

					checkValue(script, commandNode, target, "target instance");

					target.deleteEffects(containerID);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int destroy (Instance instance)
			case DESTROY:
				if (requireParameters(commandNode, 1)) {
					Instance instance = parameters[0].getInstance();

					checkValue(script, commandNode, instance, "target instance");

					instance.destroy();
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& list eachTile ()
			case EACH_TILE:
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
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& list eachEntity ()
			case EACH_ENTITY:
				List<Variable> entities = new ArrayList<>(Data.getPublicInstanceList().size()/2);
				for (Instance instance : Data.getInstanceQueue()) {
					entities.add(new Variable(instance));
				}
				return new Variable(entities);

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& list eachCreature ()
			case EACH_CREATURE:
				if (Data.getPublicInstanceList() == null) {
					return new Variable(new ArrayList<Variable>(0));
				}

				List<Variable> creatures = new ArrayList<>(Data.getPublicInstanceList().size()/2);
				for (Instance instance : Data.getInstanceQueue()) {
					if (Data.getContainer(instance.getId()).getType() == DataType.CREATURE) {
						creatures.add(new Variable(instance));
					}
				}
				return new Variable(creatures);

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int fitTiles ()
			case FIT_TILES:
				if (Data.getPlanet() != null) {
					TopologyGenerator.fitTiles(Data.getPlanet());
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int floor ()
			case FLOOR:
				if (requireParameters(commandNode, 1)) {
					return new Variable(Math.floor(parameters[0].getDouble()));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int getAttributeValue ([Instance instance,] String attribute)
			case GET_ATTRIBUTE:
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
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance getAttInRange (String attributeTextID, Tile center, int radius)
			case GET_ATTRIBUTE_IN_RANGE:
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
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& list getAttsInRange (String attributeTextID, Tile center, int radius)
			case GET_ATTRIBUTES_IN_RANGE:
				if (requireParameters(commandNode, 3)) {
					String attributeTextID = parameters[0].getString();
					Tile center = parameters[1].getTile();
					int radius = parameters[2].getInt();

					int attributeID = Data.getProtoAttributeID(attributeTextID);

					checkValue(script, commandNode, center, "center tile");
					checkAttribute(script, commandNode, attributeID, attributeTextID);

					TileArea tileArea = new TileArea(center, radius);
					List<Variable> instanceList = new ArrayList<>();
					for (Tile tile : tileArea.getTileList()) {
						Instance instance = tile.getSubInstanceWithAttribute(attributeID);

						if (instance != null) {
							instanceList.add(new Variable(instance));
						}
					}
					return new Variable(instanceList);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& list getCreaturesInRange (Tile center, int radius)
			case GET_CREATURES_IN_RANGE:
				if (requireParameters(commandNode, 2)) {
					Tile center = parameters[0].getTile();
					int radius = parameters[1].getInt();

					checkValue(script, commandNode, center, "center tile");

					List<Variable> creatures2 = new ArrayList<>();
					TileArea tileArea = new TileArea(center, radius);
					for (Tile tile : tileArea.getTileList()) {
						if (tile.getSubInstances() != null) {
							for (Instance sub : tile.getSubInstances()) {
								if (Data.getContainer(sub.getId()).getType() == DataType.CREATURE) {
									creatures2.add(new Variable(sub));
								}
							}
						}
					}
					return new Variable(creatures2);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& Effect getEffect (Instance target, Container effectContainer)
			case GET_EFFECT:
				if (requireParameters(commandNode, 1)) {
					Instance target = parameters[0].getInstance();
					int containerID = parameters.length >= 2 ? parameters[1].getContainerId() : -1;

					checkValue(script, commandNode, target, "target instance");

					List<Variable> effects = new ArrayList<>();
					if (target.getEffects() != null) {
						for (Instance effect : target.getEffects()) {
							if ((containerID == -1) || (containerID == effect.getId())) {
								return new Variable(effect);
							}
						}
					}
					return new Variable(0);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& List<Instance> getEffects (Instance target, Container effectContainer)
			case GET_EFFECTS:
				if (requireParameters(commandNode, 1)) {
					Instance target = parameters[0].getInstance();
					int containerID = parameters.length >= 2 ? parameters[1].getContainerId() : -1;

					checkValue(script, commandNode, target, "target instance");

					List<Variable> effects = new ArrayList<>();
					if (target.getEffects() != null) {
						for (Instance effect : target.getEffects()) {
							if ((containerID == -1) || (containerID == effect.getId())) {
								effects.add(new Variable(effect));
							}
						}
					}
					return new Variable(effects);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int getFullAtt (Instance instance, String attributeTextID)
			case GET_FULL_ATTRIBUTE:
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
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& Instance getHolder(Instance target)
			case GET_HOLDER:
				if (requireParameters(commandNode, 1)) {
					Instance target = parameters[0].getInstance();

					checkValue(script, commandNode, target, "target instance");

					return new Variable(target.getSuperInstance());
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int getHeight (Tile tile)
			case GET_HEIGHT:
				if (requireParameters(commandNode, 1)) {
					Tile tile = parameters[0].getTile();

					checkValue(script, commandNode, tile, "target tile");

					return new Variable(tile.getHeight());
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance getInstance (Container type, Instance instance)
			case GET_INSTANCE:
				if (requireParameters(commandNode, 2)) {
					Container type = parameters[0].getContainer();
					int containerID = -1;
					Instance instance = parameters[1].getInstance();

					checkValue(script, commandNode, instance, "target instance");
					containerID = checkType(script, commandNode, type, parameters[0].getString());

					return new Variable(instance.getThisOrSubInstanceWithID(containerID));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance getInstanceInRange (Container type, Tile center, int radius)
			case GET_INSTANCE_IN_RANGE:
				if (requireParameters(commandNode, 3)) {
					Container type = parameters[0].getContainer();
					int containerID = -1;
					Tile center = parameters[1].getTile();
					int radius = parameters[2].getInt();

					checkValue(script, commandNode, center, "center tile");
					containerID = checkType(script, commandNode, type, parameters[0].getString());

					TileArea tileArea = new TileArea(center, radius);
					for (Tile tile : tileArea.getTileList()) {
						if (tile != null) {
							Instance instance = tile.getThisOrSubInstanceWithID(containerID);

							if (instance != null) {
								return new Variable(instance);
							}
						}
					}
					return new Variable();
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance[] getInstancesInRange (Container type, Tile center, int radius)
			case GET_INSTANCES_IN_RANGE:
				if (requireParameters(commandNode, 3)) {
					Container type = parameters[0].getContainer();
					int containerID = -1;
					Tile center = parameters[1].getTile();
					int radius = parameters[2].getInt();

					checkValue(script, commandNode, center, "center tile");
					containerID = checkType(script, commandNode, type, parameters[0].getString());

					List<Variable> creatures2 = new ArrayList<>();
					TileArea tileArea = new TileArea(center, radius);
					for (Tile tile : tileArea.getTileList()) {
						Instance instance = tile.getThisOrSubInstanceWithID(containerID);

						if (instance != null) {
							creatures2.add(new Variable(instance));
						}
					}
					return new Variable(creatures2);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance getItemAtt (Instance holder, String attributeTextID)
			case GET_ITEM_ATTRIBUTE:
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
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& List<instance> getItems (Instance holder [, Container type])
			case GET_ITEMS:
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
					return new Variable(new ArrayList<>(0));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& double getLightLevel (Tile tile)
			case GET_LIGHT_LEVEL:
				if (requireParameters(commandNode, 1)) {
					Tile tile = parameters[0].getTile();

					checkValue(script, commandNode, tile, "target tile");

					Vector3 sunPosition = Data.getSun().getGraphicalObject().getPosition();//.normalize();
					Vector3 tilePosition = tile.getTileMesh().getNormal();

					double dotProduct = sunPosition.dot(tilePosition); // ranges from -sunPosition.length^2 to sunPosition.length^2
					dotProduct = Math.signum(dotProduct) * dotProduct*dotProduct / sunPosition.lengthSquared();

					return new Variable(50d * (dotProduct + 1d)); // ranges from 0 to 100
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& tile getNeighbor (Tile tile, int position)
			case GET_NEIGHBOUR:
				if (requireParameters(commandNode, 2)) {
					Tile tile = parameters[0].getTile();
					int position = parameters[1].getInt();

					checkValue(script, commandNode, tile, "target tile");

					if (position < 0 || position > 2) {
						position = position % 3;
					}

					return new Variable(Neighbour.getNeighbour(tile, position));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& list<tile> getNeighbors (Tile tile)
			case GET_NEIGHBOURS:
				if (requireParameters(commandNode, 1)) {
					Tile tile = parameters[0].getTile();

					checkValue(script, commandNode, tile, "target tile");

					List<Variable> tileList = new ArrayList<>(3);
					for (Tile neighbour : Neighbour.getNeighbours(tile)) {
						tileList.add(new Variable(neighbour));
					}
					return new Variable(tileList);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& tile getTile (Instance instance)
			case GET_TILE:
				if (requireParameters(commandNode, 1)) {
					Instance instance = parameters[0].getInstance();

					checkValue(script, commandNode, instance, "target instance");

					return new Variable(instance.getPosition());
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& list<tile> getTilesInRange (Tile tile, int radius)
			case GET_TILES_IN_RANGE:
				if (requireParameters(commandNode, 2)) {
					Tile center = parameters[0].getTile();
					int radius = parameters[1].getInt();

					checkValue(script, commandNode, center, "center tile");

					TileArea tileArea = new TileArea(center, radius);
					return new Variable(tileArea.getTilesAsVariableList());
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& Container getType (Instance instance)
			case GET_TYPE:
				if (requireParameters(commandNode,1)) {
					Instance instance = parameters[0].getInstance();

					checkValue(script, commandNode, instance, "target instance");

					return new Variable(Data.getContainer(instance.getId()));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int getWaterHeight (Tile tile)
			case GET_WATER_HEIGHT:
				if (requireParameters(commandNode, 1)) {
					Tile tile = parameters[0].getTile();

					checkValue(script, commandNode, tile, "target tile");

					return new Variable(tile.getWaterHeight());
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& boolean hasEffect (Instance target, Container effectContainer)
			case HAS_EFFECT:
				if (requireParameters(commandNode, 1)) {
					Instance target = parameters[0].getInstance();
					int containerID2 = parameters.length >= 2 ? parameters[1].getContainerId() : -1;

					checkValue(script, commandNode, target, "target instance");

					if (target.getEffects() != null) {
						for (Instance effect : target.getEffects()) {
							if ((containerID2 == -1) || (containerID2 == effect.getId())) {
								return new Variable(1);
							}
						}
					}
					return new Variable(0);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& list<tile> isNeighbor (Tile origin, Tile target)
			case IS_NEIGHBOUR:
				if (requireParameters(commandNode, 1)) {
					Tile origin = parameters[0].getTile();
					Tile target = parameters[1].getTile();

					checkValue(script, commandNode, origin, "origin tile");
					checkValue(script, commandNode, target, "target tile");

					if (origin == target) {
						return new Variable(1);
					}

					for (Tile neighbour : Neighbour.getNeighbours(origin)) {
						if (origin == neighbour) {
							return new Variable(1);
						}
					}
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& boolean isOnFloor(Instance instance)
			case IS_ON_FLOOR:
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
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& boolean isSelected(Instance instance)
			case IS_SELECTED:
				if (requireParameters(commandNode,1)) {
					Instance instance = parameters[0].getInstance();

					checkValue(script, commandNode, instance, "target instance");

					return new Variable(instance == GameOptions.selectedInstance);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int length (List list)
			case LENGTH:
				if (requireParameters(commandNode,1)) {
					List<Variable> list = parameters[0].getList();

					checkValue(script, commandNode, list, "list");

					return new Variable(list.size());
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& double max (double d1, double d2, ...)
			case MAX:
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
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& double min (double d1, double d2, ...)
			case MIN:
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
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void mixAttributes (Instance t, Instance p1, Instance p2)
			case MIX_ATTRIBUTES:
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
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& tile moveTo (Instance instance, Tile tile, int steps)
			case MOVE_TO:
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
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance pickUp (Instance holder, Instance item)
			case PICK_UP:
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
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& string print (String text)
			case PRINT:
				if (requireParameters(commandNode, 1)) {
					String text = parameters[0].getString();
					Logger.log("PRINT: " + text);
					return new Variable(text);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int random ([int bottom,] int top)
			case RANDOM:
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
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& tile randomTile ()
			case RANDOM_TILE:
				Planet planet2 = Data.getPlanet();
				if (planet2 != null) {
					int f = (int) (Math.random() * 20d);
					int x = (int) (Math.random() * planet2.getSize());
					int y = (int) (Math.random() * planet2.getSize());

					Tile tile = planet2.getFace(f).getTile(x,y);
					return new Variable(tile);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& variable return (variable)
			case RETURN:
				if (parameters.length >= 1) {
					throw new ReturnException(parameters[0]);
				}

				throw new ReturnException();

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int require (variable)
			case REQUIRE:
				if (requireParameters(commandNode, 1)) {
					boolean fulfilled = !parameters[0].isNull();
					if (!fulfilled) {
						throw new ReturnException(new Variable(0));
					}
					return new Variable(1);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int setHeight (int height)
			case SET_HEIGHT:
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
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void setMesh (Instance target, String path)
			case SET_MESH:
				if (requireParameters(commandNode, 2)) {
					Instance target = parameters[0].getInstance();
					String path = parameters[1].getString();

					checkValue(script, commandNode, target, "target instance");

					target.setMesh(ResourcePathConstants.MOD_FOLDER + path);

					return new Variable("Semira <3");
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& string setSunAngle (double angle)
			case SET_SUN_ANGLE:
				if (requireParameters(commandNode, 1)) {
					double angle = parameters[0].getDouble();

					Data.getSun().setAngle(angle);

					return new Variable("Semira <3");
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int setWaterHeight (int height)
			case SET_WATER_HEIGHT:
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
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int setWaterLevel (int level)
			case SET_WATER_LEVEL:
				if (requireParameters(commandNode, 1)) {
					Planet planet3 = Data.getPlanet();
					if (planet3 != null) {
						int level = (int) (parameters[0].getDouble());
						for (Face face : planet3.getFaces()) {
							for (Tile tile : face.getTiles()) {
								tile.setWaterHeight(level);
							}
						}

						return new Variable(level);
					}
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void updatePlanetMesh ()
			case UPDATE_PLANET_MESH:
				Data.setUpdatePlanetMesh(true);
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& UNKNOWN COMMAND!
			default:
				Logger.error(
						"Unknown command '" + commandNode.getCommand() + "'!" +
						" Error on line " + commandNode.getCommand().getLine() +
						" in command '" + commandNode.getCommand().getValue() + "'" +
						" during execution of script '" + script.getTextId() + "' in file '" + script.getFileName() + "'."
				);
				break;

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
