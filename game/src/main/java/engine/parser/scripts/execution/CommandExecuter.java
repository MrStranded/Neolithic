package engine.parser.scripts.execution;

import constants.ResourcePathConstants;
import constants.ScriptConstants;
import constants.TopologyConstants;
import engine.data.Data;
import engine.data.attributes.Attribute;
import engine.data.entities.Effect;
import engine.data.entities.GuiElement;
import engine.data.entities.Instance;
import engine.data.entities.Tile;
import engine.data.options.GameOptions;
import engine.data.planetary.Face;
import engine.data.planetary.Planet;
import engine.data.proto.Container;
import engine.data.proto.ProtoAttribute;
import engine.data.scripts.Script;
import engine.data.structures.trees.binary.BinaryTree;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.graphics.gui.GuiData;
import engine.graphics.renderer.color.RGBA;
import engine.logic.topology.*;
import engine.math.numericalObjects.Vector3;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.exceptions.InvalidValueException;
import engine.parser.scripts.exceptions.ReturnException;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.nodes.CommandExpressionNode;
import engine.parser.tokenization.Token;
import engine.parser.utils.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class CommandExecuter {

	public static Variable executeCommand(Instance self, Script script, CommandExpressionNode commandNode) throws ScriptInterruptedException {
		Token command = commandNode.getCommand();
		Variable[] parameters = ParameterCalculator.calculateParameters(self, script, commandNode);

		TokenConstants commandToken = TokenConstants.getCorrespondingConstant(command);
		if (commandToken == null) { return new Variable(); }

		if (commandToken.getCommandExecutor() != null) {
			ScriptContext context = new ScriptContext(self, script, commandNode);

			return commandToken.getCommandExecutor().execute(context, parameters);
		}

		switch (commandToken) {

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& double atan (double tan)
			case ATAN:
				if (requireParameters(commandNode, 1)) {
					double tan = parameters[0].getDouble();
					return new Variable(Math.atan(tan) * 180d / Math.PI);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& double atan2 (double y, double x)
			case ATAN2:
				if (requireParameters(commandNode, 1)) {
					double y = parameters[0].getDouble();
					double x = parameters[1].getDouble();
					return new Variable(Math.atan2(y, x) * 180d / Math.PI);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void breakpoint ()
			case BREAKPOINT:
				if (parameters.length > 0) {
					Arrays.stream(parameters).forEach(parameter -> {
						Logger.breakpoint(parameter.toString());
					});
				}

				Logger.breakpoint("Breakpoint on line " + command.getLine() + " in " + script.getTextId() + " (" + script.getFileName() + ")");
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int ceil ()
			case CEIL:
				if (requireParameters(commandNode, 1)) {
					return new Variable(Math.ceil(parameters[0].getDouble()));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& boolean chance (double probability)
			case CHANCE:
				if (requireParameters(commandNode, 1)) {
					double chance = parameters[0].getDouble();

					return new Variable(Math.random() < chance ? 1 : 0);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& Instance change (Instance target, Container / String container)
			case CHANGE:
				if (requireParameters(commandNode, 2)) {
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

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void clearGui ()
			case CLEAR_GUI:
				if (parameters.length > 0) {
					GuiElement element = parameters[0].getGuiElement();

					checkValue(script, commandNode, element, "gui element");

					element.getSubElements().forEach(GuiElement::destroy);

				} else {

					GuiData.getHud().clear();
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

					int containerId = checkType(script, commandNode, container, parameters[0].getString());
					checkValue(script, commandNode, holder, "holder instance");

					Instance instance = Data.addInstanceToQueue(new Instance(containerId));
					instance.placeInto(holder);

					instance.run(ScriptConstants.EVENT_NEW, new Variable[] { parameters[1] });

					return new Variable(instance);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& guiElement createGui (Container container [, parameters ... ])
			case CREATE_GUI:
				if (requireParameters(commandNode, 1)) {
					Container container = parameters[0].getContainer();

					int containerId = checkType(script, commandNode, container, parameters[0].getString());

					GuiElement element = new GuiElement(containerId);
					GuiData.getHud().addElement(element);

					if (parameters.length > 1) {
						element.run(ScriptConstants.EVENT_NEW, Arrays.copyOfRange(parameters, 1, parameters.length));
					} else {
						element.run(ScriptConstants.EVENT_NEW, new Variable[] {});
					}

					return new Variable(element);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& double cos (double radian)
			case COS:
				if (requireParameters(commandNode, 1)) {
					double radian = parameters[0].getDouble() * Math.PI / 180d;
					return new Variable(Math.cos(radian));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void delayNextTick (Instance target, int ticks)
			case DELAY_NEXT_TICK:
				if (requireParameters(commandNode, 2)) {
					Instance target = parameters[0].getInstance();
					int ticks = parameters[1].getInt();

					checkValue(script, commandNode, target, "target instance");

					target.setDelayUntilNextTick(ticks);
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

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& list eachAttribute (Instance instance)
			case EACH_ATTRIBUTE:
				if (requireParameters(commandNode, 1)) {
					Instance instance = parameters[0].getInstance();

					checkValue(script, commandNode, instance, "target instance");

					return new Variable(Data.getAllAttributeIDs().stream()
							.map(id -> new int[] {id, instance.getAttributeValue(id), instance.getPersonalAttributeValue(id)})
							.filter(idValueTuple -> idValueTuple[1] != 0 || idValueTuple[2] != 0)
							.map(idValueTuple -> new Attribute(idValueTuple[0], idValueTuple[1]))
							.map(Variable::new)
							.collect(Collectors.toList()));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& list eachVariable (Instance instance)
			case EACH_VARIABLE:
				if (requireParameters(commandNode, 1)) {
					Instance instance = parameters[0].getInstance();

					checkValue(script, commandNode, instance, "target instance");

					BinaryTree<Variable> variables = instance.getVariables();
					if (variables == null) { return new Variable(Collections.emptyList()); }

					List<Variable> list = new ArrayList<>(variables.size());
					variables.forEach(list::add);
					return new Variable(list);
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
				return new Variable(Data.getInstanceQueue().stream()
						.map(Variable::new)
						.collect(Collectors.toList()));

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& list eachCreature ()
			case EACH_CREATURE:
				return new Variable(Data.getInstanceQueue().stream()
						.filter(instance ->
								instance.getContainer()
										.map(c -> c.getType() == DataType.CREATURE)
										.orElse(false))
						.map(Variable::new)
						.collect(Collectors.toList()));

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int fitTiles ()
			case FIT_TILES:
				if (Data.getPlanet() != null) {
					List<Container> tileList;

					if (parameters.length > 0) {
						tileList = new ArrayList<>();

						for (Variable parameter : parameters) {
							Container tileContainer = parameter.getContainer();
							checkType(script, commandNode, tileContainer, parameter.getString());

							if (tileContainer.getType() == DataType.TILE) {
								tileList.add(tileContainer);
							}
						}

					} else {
						tileList = Data.getContainersOfType(DataType.TILE);
					}

					TopologyGenerator.fitTiles(Data.getPlanet(), tileList);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int floor ()
			case FLOOR:
				if (requireParameters(commandNode, 1)) {
					return new Variable(Math.floor(parameters[0].getDouble()));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void forget ()
			case FREE_VARIABLE:
				if (requireParameters(commandNode, 1)) {
					parameters[0].invalidate();
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int getAtt ([Instance instance,] String | int attribute)
			case GET_ATTRIBUTE:
				if (parameters.length >= 2) {
					Instance instance = parameters[0].getInstance();
					checkValue(script, commandNode, instance, "target instance");

					int attributeID;
					if (parameters[1].getType() == DataType.NUMBER) {
						attributeID = parameters[1].getInt();

					} else {
						String attributeTextID = parameters[1].getString();
						attributeID = Data.getProtoAttributeID(attributeTextID);

						checkAttribute(script, commandNode, attributeID, attributeTextID);
					}

					return new Variable(instance.getAttribute(attributeID));
				} else if (requireParameters(commandNode, 1)) {
					int attributeID;
					if (parameters[0].getType() == DataType.NUMBER) {
						attributeID = parameters[0].getInt();

					} else {
						String attributeTextID = parameters[0].getString();
						attributeID = Data.getProtoAttributeID(attributeTextID);

						checkAttribute(script, commandNode, attributeID, attributeTextID);
					}

					return new Variable(self.getAttribute(attributeID));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int getAttValue ([Instance instance,] String | int attribute)
			case GET_ATTRIBUTE_VALUE:
				if (parameters.length >= 2) {
					Instance instance = parameters[0].getInstance();
					checkValue(script, commandNode, instance, "target instance");

					int attributeID;
					if (parameters[1].getType() == DataType.NUMBER) {
						attributeID = parameters[1].getInt();

					} else {
						String attributeTextID = parameters[1].getString();
						attributeID = Data.getProtoAttributeID(attributeTextID);

						checkAttribute(script, commandNode, attributeID, attributeTextID);
					}

					return new Variable(instance.getAttributeValue(attributeID));
				} else if (requireParameters(commandNode, 1)) {
					int attributeID;
					if (parameters[0].getType() == DataType.NUMBER) {
						attributeID = parameters[0].getInt();

					} else {
						String attributeTextID = parameters[0].getString();
						attributeID = Data.getProtoAttributeID(attributeTextID);

						checkAttribute(script, commandNode, attributeID, attributeTextID);
					}

					return new Variable(self.getAttributeValue(attributeID));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int getAttId (String | Attribute attribute)
			case GET_ATTRIBUTE_ID:
				if (requireParameters(commandNode, 1)) {
					if (parameters[0].getType() == DataType.ATTRIBUTE) {
						Attribute attribute = parameters[0].getAttribute();

						checkValue(script, commandNode, attribute, "target attribute");

						return new Variable(attribute.getId());
					}

					String attributeTextID = parameters[0].getString();
					int attributeID = Data.getProtoAttributeID(attributeTextID);

					return new Variable(attributeID);
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
								if (Data.getContainer(sub.getId()).filter(c -> c.getType() == DataType.CREATURE).isPresent()) {
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
					int containerID = checkType(script, commandNode, type, parameters[0].getString());

					Instance instance = parameters[1].getInstance();
					checkValue(script, commandNode, instance, "target instance");

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
				if (requireParameters(commandNode, 1)) {
					Instance target = parameters[0].getInstance();

					checkValue(script, commandNode, target, "holder instance");

					List<Instance> subInstances = target.getSubInstances();
					if (subInstances == null || subInstances.isEmpty()) {
						return new Variable(Collections.emptyList());
					}

					if (parameters.length < 2) {
						return new Variable(subInstances.stream()
								.map(Variable::new)
								.collect(Collectors.toList())
						);
					}

					Container filter = parameters[1].getContainer();
					return new Variable(subInstances.stream()
							.filter(sub -> sub
									.getContainer()
									.map(container -> container.equals(filter))
									.orElse(true))
							.map(Variable::new)
							.collect(Collectors.toList())
					);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& double getLatitude (Tile tile)
			case GET_LATITUDE:
				if (requireParameters(commandNode, 1)) {
					Tile tile = parameters[0].getTile();

					checkValue(script, commandNode, tile, "target tile");

					return new Variable(GeographicCoordinates.getLatitudeDegrees(tile));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& double getLightLevel (Tile tile)
			case GET_LIGHT_LEVEL:
				if (requireParameters(commandNode, 1)) {
					Tile tile = parameters[0].getTile();

					checkValue(script, commandNode, tile, "target tile");

					Vector3 sunPosition = Data.getSun().getNormalizedSunPosition();
					// nudging it just a little bit in direction of sun to have more sunlight
					Vector3 tilePosition = tile.getTileMesh().getNormal().plus(sunPosition.times(0.05));

					double dotProduct = sunPosition.dot(tilePosition);
					dotProduct = Math.max(0, dotProduct);

					return new Variable(100d * dotProduct); // ranges from 0 to 100
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& double getLongitude (Tile tile)
			case GET_LONGITUDE:
				if (requireParameters(commandNode, 1)) {
					Tile tile = parameters[0].getTile();

					checkValue(script, commandNode, tile, "target tile");

					return new Variable(GeographicCoordinates.getLongitudeDegrees(tile));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& string getMachineAddress (Instance instance)
			case GET_MEMORY_ADDRESS:
				if (requireParameters(commandNode, 1)) {
					Instance instance = parameters[0].getInstance();

					checkValue(script, commandNode, instance, "target instance");

					return new Variable(instance.getMemoryAddress());
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& string getName (Instance | Container target)
			case GET_NAME:
				if (requireParameters(commandNode, 1)) {
					Instance instance = parameters[0].getInstance();

					if (instance != null) {
						return new Variable(instance.getName());
					}

					Attribute attribute = parameters[0].getAttribute();
					if (attribute != null) {
						ProtoAttribute protoAttribute = Data.getProtoAttribute(attribute.getId());
						if (protoAttribute != null) {
							return new Variable(protoAttribute.getName());
						}
					}

					Container container = parameters[0].getContainer();
					checkValue(script, commandNode, container, "target instance, attribute or container");

					return new Variable(container.getName(null));
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

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& String getProperty (Instance target, String key)
			case GET_PROPERTY:
				if (requireParameters(commandNode, 2)) {
					Instance target = parameters[0].getInstance();
					String key = parameters[1].getString();

					checkValue(script, commandNode, target, "target instance");

					return target.getProperty(key);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& instance getOrCreate (Container container, Instance holder)
			case GET_OR_CREATE:
				if (requireParameters(commandNode, 2)) {
					Container container = parameters[0].getContainer();
					Instance holder = parameters[1].getInstance();
					int containerId = -1;

					containerId = checkType(script, commandNode, container, parameters[0].getString());
					checkValue(script, commandNode, holder, "holder instance");

					Instance instance = holder.getThisOrSubInstanceWithID(containerId);
					if (instance == null) {
						instance = Data.addInstanceToQueue(new Instance(containerId));

						instance.run(ScriptConstants.EVENT_NEW, new Variable[] { parameters[1] });
//						Data.addInstanceToQueue(instance);
					}
					instance.placeInto(holder);

					return new Variable(instance);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& String getStage (Instance instance)
			case GET_STAGE:
				if (requireParameters(commandNode,1)) {
					Instance instance = parameters[0].getInstance();

					checkValue(script, commandNode, instance, "target instance");

					return new Variable(instance.getStage());
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

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& tile getTileFromCoords (double east, double north)
			case GET_TILE_FROM_COORDINATES:
				if (requireParameters(commandNode, 2)) {
					// both assumed to be in degrees
					double east = parameters[0].getDouble();
					double north = parameters[1].getDouble();

					return new Variable(GeographicCoordinates.getTile(Data.getPlanet(), east, north));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void debug (Tile debugtile)
			case DEBUG:
				if (requireParameters(commandNode, 1)) {
					Tile tile = parameters[0].getTile();

					checkValue(script, commandNode, tile, "debug value");

					// both assumed to be in degrees
					double east = GeographicCoordinates.getLongitudeDegrees(tile);
					double north = GeographicCoordinates.getLatitudeDegrees(tile);

					Logger.debug("east: " + east);
					Logger.debug("north: " + north);

					Logger.debug("n o: " + tile.getTileMesh().getNormal());

					Tile same = GeographicCoordinates.getTile(Data.getPlanet(), east, north);

					Logger.debug("n s: " + same.getTileMesh().getNormal());

					Logger.debug("same: " + (same == tile));
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

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& list<tile> getTilesOfRange (Tile from, Tile to[, int viewingDistance])
			case GET_TILES_OF_PATH:
				if (requireParameters(commandNode, 2)) {
					Tile from = parameters[0].getTile();
					Tile to   = parameters[1].getTile();

					int viewingDistance = 7;
					if (parameters.length > 2) { viewingDistance = parameters[2].getInt(); }

					checkValue(script, commandNode, from, "origin tile");
					checkValue(script, commandNode, to, "destination tile");

					List<Variable> path = new ArrayList<>();
					if (from != to) { path.add(new Variable(from)); }

					int counter = 0;
					while (from != to) {
						from = Pathfinding.moveTowardsTile(from, to, 1, viewingDistance);
						path.add(new Variable(from));
						if (counter++ > 42) { break; }
					}

					return new Variable(path);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& Container getType (Instance instance)
			case GET_TYPE:
				if (requireParameters(commandNode,1)) {
					Instance instance = parameters[0].getInstance();

					checkValue(script, commandNode, instance, "target instance");

					return new Variable(instance.getContainer().orElse(null));
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

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& boolean inDefaultStage (Instance target)
			case IN_DEFAULT_STAGE:
				if (requireParameters(commandNode, 1)) {
					Instance target = parameters[0].getInstance();

					checkValue(script, commandNode, target, "target instance");

					return new Variable(ScriptConstants.DEFAULT_STAGE.equals(target.getStage()));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& boolean inStage (Instance target, String stage)
			case IN_STAGE:
				if (requireParameters(commandNode, 2)) {
					Instance target = parameters[0].getInstance();
					String stage = parameters[1].getString();

					checkValue(script, commandNode, target, "target instance");
					checkValue(script, commandNode, stage, "target stage");

					return new Variable(stage.equals(target.getStage()));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& boolean isCloserToEquator (Tile subject, Tile other)
			case IS_CLOSER_TO_EQUATOR:
				if (requireParameters(commandNode, 2)) {
					Tile subject = parameters[0].getTile();
					Tile challenger = parameters[1].getTile();

					checkValue(script, commandNode, subject, "subject tile");
					checkValue(script, commandNode, challenger, "challenger tile");

					Vector3 subjectPosition = subject.getTileMesh().getNormal();
					Vector3 challengerPosition = challenger.getTileMesh().getNormal();

					return new Variable(Math.abs(subjectPosition.getY()) < Math.abs(challengerPosition.getY()));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& boolean isFartherNorth (Tile subject, Tile other)
			case IS_FARTHER_NORTH:
				if (requireParameters(commandNode, 2)) {
					Tile subject = parameters[0].getTile();
					Tile challenger = parameters[1].getTile();

					checkValue(script, commandNode, subject, "subject tile");
					checkValue(script, commandNode, challenger, "challenger tile");

					Vector3 subjectPosition = subject.getTileMesh().getNormal();
					Vector3 challengerPosition = challenger.getTileMesh().getNormal();

					return new Variable(subjectPosition.getY() > challengerPosition.getY());
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& boolean isFartherSouth (Tile subject, Tile other)
			case IS_FARTHER_SOUTH:
				if (requireParameters(commandNode, 2)) {
					Tile subject = parameters[0].getTile();
					Tile challenger = parameters[1].getTile();

					checkValue(script, commandNode, subject, "subject tile");
					checkValue(script, commandNode, challenger, "challenger tile");

					Vector3 subjectPosition = subject.getTileMesh().getNormal();
					Vector3 challengerPosition = challenger.getTileMesh().getNormal();

					return new Variable(subjectPosition.getY() < challengerPosition.getY());
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
						return Data.getContainer(instance.getSuperInstance().getId())
								.filter(c -> c.getType() == DataType.TILE)
								.map(c -> new Variable(1)).orElse(new Variable());
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
				if (requireParameters(commandNode, 1)) {
					Instance target = parameters[0].getInstance();
					checkValue(script, commandNode, target, "child instance");

					List<Instance> parents = new ArrayList<>(parameters.length - 1);
					if (parameters.length > 1) {
						for (int i = 1; i < parameters.length; i++) {
							Instance parent = parameters[i].getInstance();
							checkValue(script, commandNode, parent, "parent " + i + " instance");

							parents.add(parent);
						}
					}

					target.inheritAttributes(parents);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& tile moveTo (Instance instance, Tile tile, int steps)
			case MOVE_TO:
				if (requireParameters(commandNode, 3)) {
					Instance instance = parameters[0].getInstance();
					Tile tile = parameters[1].getTile();
					int steps = parameters[2].getInt();

					int viewingDistance = steps * 4;
					if (parameters.length > 3) { viewingDistance = parameters[3].getInt(); }

					checkValue(script, commandNode, tile, "target tile");
					checkValue(script, commandNode, instance, "target instance");

					Tile newPosition = Pathfinding.moveTowardsTile(instance, tile, steps, viewingDistance);

					if (newPosition != instance.getPosition()) {
						instance.placeInto(newPosition);
					}
					return new Variable(newPosition);
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& number percent (number part, number whole)
			case PERCENT:
				if (requireParameters(commandNode, 2)) {
					double part = parameters[0].getDouble();
					double whole = parameters[1].getDouble();

					if (whole == 0) { return new Variable(); }

					return new Variable(100d * part / whole);
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
					Logger.info("PRINT: " + text);
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
					if (parameters[0].getType() == DataType.LIST) {
						List<Variable> list = parameters[0].getList();

						int value = (int) (Math.random() * list.size());
						return list.get(value);
					} else {
						double top = parameters[0].getDouble();

						int value = (int) (Math.random() * top);
						return new Variable(value);
					}
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

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& rgba rgba (r, g, b, a)
			case RGBA:
				if (requireParameters(commandNode, 4)) {
					double r = parameters[0].getDouble();
					double g = parameters[1].getDouble();
					double b = parameters[2].getDouble();
					double a = parameters[3].getDouble();

					return new Variable(new RGBA(r, g, b, a));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void select (Instance target)
			case SELECT:
				if (requireParameters(commandNode, 1)) {
					Instance target = parameters[0].getInstance();

					GameOptions.selectedInstance = target;

					return new Variable("Semira <3");
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void setDefaultStage (Instance target)
			case SET_DEFAULT_STAGE:
				if (requireParameters(commandNode, 1)) {
					Instance target = parameters[0].getInstance();

					checkValue(script, commandNode, target, "target instance");

					target.setStage(ScriptConstants.DEFAULT_STAGE);

					return new Variable("Semira <3");
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int setHeight (int height)
			case SET_HEIGHT:
				if (requireParameters(commandNode, 2)) {
					Tile tile = parameters[0].getTile();
					int height = parameters[1].getInt();

					if (tile != null) {
						if (height < 0) { height = 0; }
						if (height > TopologyConstants.PLANET_HEIGHT_RANGE) { height = (int) TopologyConstants.PLANET_HEIGHT_RANGE; }
						tile.setHeight(height);

						return new Variable(height);
					}
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& int setLevel (int level)
			case SET_LEVEL:
				if (requireParameters(commandNode, 1)) {
					if (Data.getPlanet() != null) {
						int level = (int) (parameters[0].getDouble());
						for (Face face : Data.getPlanet().getFaces()) {
							for (Tile tile : face.getTiles()) {
								tile.setHeight(level);
							}
						}

						return new Variable(level);
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

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void setStage (Instance target, String stage)
			case SET_STAGE:
				if (requireParameters(commandNode, 2)) {
					Instance target = parameters[0].getInstance();
					String stage = parameters[1].getString();

					checkValue(script, commandNode, target, "target instance");
					checkValue(script, commandNode, stage, "target stage");

					target.setStage(stage);

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
						if (height > TopologyConstants.PLANET_HEIGHT_RANGE) { height = (int) TopologyConstants.PLANET_HEIGHT_RANGE; }
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

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& double sin (double radian)
			case SIN:
				if (requireParameters(commandNode, 1)) {
					double radian = parameters[0].getDouble() * Math.PI / 180d;
					return new Variable(Math.sin(radian));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& double tan (double radian)
			case TAN:
				if (requireParameters(commandNode, 1)) {
					double radian = parameters[0].getDouble() * Math.PI / 180d;
					return new Variable(Math.tan(radian));
				}
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& void updatePlanetMesh ()
			case UPDATE_PLANET_MESH:
				Data.setUpdatePlanetMesh(true);
				break;

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& UNKNOWN COMMAND!
			default:
				Logger.error(
						"Unknown command '" + commandNode.getCommand() + "'!" + System.lineSeparator() +
						" Error on line " + commandNode.getCommand().getLine() + System.lineSeparator() +
						" in command '" + commandNode.getCommand().getValue() + "'" + System.lineSeparator() +
						" during execution of script '" + script.getTextId() + "' in file '" + script.getFileName() + "'."
				);
				break;

		}

		return new Variable();
	}

	private static void checkValue(Script script, CommandExpressionNode commandNode, Object value, String objectName) throws InvalidValueException {
        if (value == null) {
        	Logger.executionError(
        			"Value '" + objectName + "' is empty!",
					commandNode.getCommand(), script);
            throw new InvalidValueException("Value '" + objectName + "' is invalid!");
        }
    }

    private static void checkAttribute(Script script, CommandExpressionNode commandNode, int attributeID, String attributeTextID) throws InvalidValueException {
        if (attributeID == -1) {
        	Logger.executionError(
        			"Attribute '" + attributeTextID + "' does not exist!",
					commandNode.getCommand(), script);
            throw new InvalidValueException("Attribute '" + attributeTextID + "' does not exist!");
        }
    }

    private static int checkType(Script script, CommandExpressionNode commandNode, Container type, String typeName) throws InvalidValueException {
	    int containerID = -1;

        if ((type == null) || ((containerID = Data.getContainerID(type.getTextID())) < 0)) {
        	Logger.executionError(
        			"Type with name '" + typeName + "' does not exist!",
					commandNode.getCommand(), script);
            throw new InvalidValueException("Type with name '" + typeName + "' does not exist!");
        }

        return containerID;
    }

	private static boolean requireParameters(CommandExpressionNode commandNode, int amount) {
		if (amount > 0 && (commandNode.getSubNodes() == null || amount > commandNode.getSubNodes().length)) {

			Logger.executionError(
					"The command '" + commandNode.getCommand().getValue() + "' needs at least " + amount + " parameters!",
					commandNode.getCommand());
			return false;
		}
		return true;
	}

}
