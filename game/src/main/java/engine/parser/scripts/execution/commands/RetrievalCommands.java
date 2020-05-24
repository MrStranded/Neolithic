package engine.parser.scripts.execution.commands;

import engine.data.Data;
import engine.data.entities.Instance;
import engine.data.options.GameOptions;
import engine.data.planetary.Planet;
import engine.data.planetary.Tile;
import engine.data.proto.Container;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.logic.topology.Neighbour;
import engine.logic.topology.TileArea;
import engine.math.numericalObjects.Vector3;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.execution.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RetrievalCommands implements CommandProvider {

    @Override
    public List<Command> buildCommands() {
        return Arrays.asList(
                //  int getAttributeValue (String attribute [,Instance instance])
                new Command(TokenConstants.GET_ATTRIBUTE.getValue(), 1, (self, parameters) -> {
                    int attributeID = CommandUtils.getAndCheckAttributeID(parameters[0]);

                    Instance target = self;
                    if (parameters.length >= 2) {
                        Instance instance = parameters[1].getInstance();
                        CommandUtils.checkValueExists(instance, "target instance");
                    }

                    return new Variable(target.getAttribute(attributeID));
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& instance getAttInRange (String attributeTextID, Tile center, int radius)
                new Command(TokenConstants.GET_ATTRIBUTE_IN_RANGE.getValue(), 3, (self, parameters) -> {
                    int attributeID = CommandUtils.getAndCheckAttributeID(parameters[0]);
                    Tile center = parameters[1].getTile();
                    CommandUtils.checkValueExists(center, "center tile");
                    int radius = parameters[2].getInt();

                    TileArea tileArea = new TileArea(center, radius);
                    for (Tile tile : tileArea.getTileList()) {
                        Instance instance = tile.getSubInstanceWithAttribute(attributeID);

                        if (instance != null) {
                            return new Variable(instance);
                        }
                    }
                    return new Variable();
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& list getAttsInRange (String attributeTextID, Tile center, int radius)
                new Command(TokenConstants.GET_ATTRIBUTES_IN_RANGE.getValue(), 3, (self, parameters) -> {
                    int attributeID = CommandUtils.getAndCheckAttributeID(parameters[0]);
                    Tile center = parameters[1].getTile();
                    CommandUtils.checkValueExists(center, "center tile");
                    int radius = parameters[2].getInt();

                    TileArea tileArea = new TileArea(center, radius);
                    List<Variable> instanceList = new ArrayList<>();
                    for (Tile tile : tileArea.getTileList()) {
                        Instance instance = tile.getSubInstanceWithAttribute(attributeID);

                        if (instance != null) {
                            instanceList.add(new Variable(instance));
                        }
                    }
                    return new Variable(instanceList);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& list getCreaturesInRange (Tile center, int radius)
                new Command(TokenConstants.GET_CREATURES_IN_RANGE.getValue(), 2, (self, parameters) -> {
                    Tile center = parameters[0].getTile();
                    CommandUtils.checkValueExists(center, "center tile");

                    int radius = parameters[1].getInt();

                    List<Variable> creatures = new ArrayList<>();
                    TileArea tileArea = new TileArea(center, radius);
                    for (Tile tile : tileArea.getTileList()) {
                        if (tile.getSubInstances() != null) {
                            for (Instance sub : tile.getSubInstances()) {
                                Container container = Data.getContainer(sub.getId());
                                if (container != null && container.getType() == DataType.CREATURE) {
                                    creatures.add(new Variable(sub));
                                }
                            }
                        }
                    }
                    return new Variable(creatures);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& Effect getEffect (Instance target, Container effectContainer)
                new Command(TokenConstants.GET_EFFECT.getValue(), 1, (self, parameters) -> {
                    Instance target = parameters[0].getInstance();
                    CommandUtils.checkValueExists(target, "target instance");
                    int containerID = parameters.length >= 2 ? CommandUtils.getAndCheckContainerID(parameters[1]) : -1;

                    if (target.getEffects() != null) {
                        for (Instance effect : target.getEffects()) {
                            if ((containerID == -1) || (containerID == effect.getId())) {
                                return new Variable(effect);
                            }
                        }
                    }
                    return new Variable(0);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& List<Instance> getEffects (Instance target, Container effectContainer)
                new Command(TokenConstants.GET_EFFECTS.getValue(), 1, (self, parameters) -> {
                    Instance target = parameters[0].getInstance();
                    CommandUtils.checkValueExists(target, "target instance");
                    int containerID = parameters.length >= 2 ? CommandUtils.getAndCheckContainerID(parameters[1]) : -1;

                    List<Variable> effects = new ArrayList<>();
                    if (target.getEffects() != null) {
                        for (Instance effect : target.getEffects()) {
                            if ((containerID == -1) || (containerID == effect.getId())) {
                                effects.add(new Variable(effect));
                            }
                        }
                    }
                    return new Variable(effects);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& int getFullAtt (String attributeTextID [,Instance instance])
                new Command(TokenConstants.GET_FULL_ATTRIBUTE.getValue(), 1, (self, parameters) -> {
                    int attributeID = CommandUtils.getAndCheckAttributeID(parameters[0]);

                    Instance target = self;
                    if (parameters.length >= 2) {
                        target = parameters[1].getInstance();
                        CommandUtils.checkValueExists(target, "target instance");
                    }

                    return new Variable(target.getFullAttributeValue(attributeID));
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& Instance getHolder(Instance target)
                new Command(TokenConstants.GET_HOLDER.getValue(), 1, (self, parameters) -> {
                    Instance target = parameters[0].getInstance();
                    CommandUtils.checkValueExists(target, "target instance");
                    return new Variable(target.getSuperInstance());
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& int getHeight (Tile tile)
                new Command(TokenConstants.GET_HEIGHT.getValue(), 1, (self, parameters) -> {
                    Tile tile = parameters[0].getTile();
                    CommandUtils.checkValueExists(tile, "target tile");
                    return new Variable(tile.getHeight());
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& instance getInstance (Container type, Instance instance)
                new Command(TokenConstants.GET_INSTANCE.getValue(), 2, (self, parameters) -> {
                    int containerID = CommandUtils.getAndCheckContainerID(parameters[0]);
                    Instance instance = parameters[1].getInstance();
                    CommandUtils.checkValueExists(instance, "target instance");
                    return new Variable(instance.getThisOrSubInstanceWithID(containerID));
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& instance getInstanceInRange (Container type, Tile center, int radius)
                new Command(TokenConstants.GET_INSTANCE_IN_RANGE.getValue(), 3, (self, parameters) -> {
                    int containerID = CommandUtils.getAndCheckContainerID(parameters[0]);
                    Tile center = parameters[1].getTile();
                    CommandUtils.checkValueExists(center, "center tile");
                    int radius = parameters[2].getInt();

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
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& instance[] getInstancesInRange (Container type, Tile center, int radius)
                new Command(TokenConstants.GET_INSTANCES_IN_RANGE.getValue(), 3, (self, parameters) -> {
                    int containerID = CommandUtils.getAndCheckContainerID(parameters[0]);
                    Tile center = parameters[1].getTile();
                    CommandUtils.checkValueExists(center, "center tile");
                    int radius = parameters[2].getInt();

                    List<Variable> creatures = new ArrayList<>();
                    TileArea tileArea = new TileArea(center, radius);
                    for (Tile tile : tileArea.getTileList()) {
                        Instance instance = tile.getThisOrSubInstanceWithID(containerID);

                        if (instance != null) {
                            creatures.add(new Variable(instance));
                        }
                    }
                    return new Variable(creatures);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& instance getItemAtt (Instance holder, String attributeTextID)
                new Command(TokenConstants.GET_ITEM_ATTRIBUTE.getValue(), 2, (self, parameters) -> {
                    Instance holder = parameters[0].getInstance();
                    CommandUtils.checkValueExists(holder, "holder instance");
                    int attributeID = CommandUtils.getAndCheckAttributeID(parameters[1]);

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
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& List<instance> getItems (Instance holder [, Container type])
                new Command(TokenConstants.GET_ITEMS.getValue(), 1, (self, parameters) -> {
                    Instance holder = parameters[0].getInstance();
                    CommandUtils.checkValueExists(holder, "holder instance");

                    int containerID = -1;
                    if (parameters.length >= 2) {
                        containerID = CommandUtils.getAndCheckContainerID(parameters[1]);
                    }

                    if (holder.getSubInstances() != null) {
                        List<Variable> items = new ArrayList<>(holder.getSubInstances().size());
                        for (Instance sub : holder.getSubInstances()) {
                            if (sub != null && (containerID == -1 || containerID == sub.getId())) {
                                items.add(new Variable(sub));
                            }
                        }
                        return new Variable(items);
                    }
                    return Variable.emptyList();
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& double getLightLevel (Tile tile)
                new Command(TokenConstants.GET_LIGHT_LEVEL.getValue(), 1, (self, parameters) -> {
                    Tile tile = parameters[0].getTile();
                    CommandUtils.checkValueExists(tile, "target tile");

                    Vector3 sunPosition = Data.getSun().getGraphicalObject().getPosition();//.normalize();
                    Vector3 tilePosition = tile.getTileMesh().getNormal();

                    double dotProduct = sunPosition.dot(tilePosition); // ranges from -sunPosition.length^2 to sunPosition.length^2
                    dotProduct = Math.signum(dotProduct) * dotProduct*dotProduct / sunPosition.lengthSquared();

                    return new Variable(50d * (dotProduct + 1d)); // ranges from 0 to 100
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& tile getNeighbor (Tile tile, int position)
                new Command(TokenConstants.GET_NEIGHBOUR.getValue(), 2, (self, parameters) -> {
                    Tile tile = parameters[0].getTile();
                    CommandUtils.checkValueExists(tile, "target tile");
                    int position = parameters[1].getInt() % 3;

                    return new Variable(Neighbour.getNeighbour(tile, position));
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& list<tile> getNeighbors (Tile tile)
                new Command(TokenConstants.GET_NEIGHBOURS.getValue(), 1, (self, parameters) -> {
                    Tile tile = parameters[0].getTile();
                    CommandUtils.checkValueExists(tile, "target tile");

                    List<Variable> tileList = new ArrayList<>(3);
                    for (Tile neighbour : Neighbour.getNeighbours(tile)) {
                        tileList.add(new Variable(neighbour));
                    }
                    return new Variable(tileList);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& tile getTile (Instance instance)
                new Command(TokenConstants.GET_TILE.getValue(), 1, (self, parameters) -> {
                    Instance instance = parameters[0].getInstance();
                    CommandUtils.checkValueExists(instance, "target instance");
                    return new Variable(instance.getPosition());
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& list<tile> getTilesInRange (Tile tile, int radius)
                new Command(TokenConstants.GET_TILES_IN_RANGE.getValue(), 2, (self, parameters) -> {
                    Tile center = parameters[0].getTile();
                    CommandUtils.checkValueExists(center, "center tile");
                    int radius = parameters[1].getInt();

                    TileArea tileArea = new TileArea(center, radius);
                    return new Variable(tileArea.getTilesAsVariableList());
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& Container getType (Instance instance)
                new Command(TokenConstants.GET_TYPE.getValue(), 1, (self, parameters) -> {
                    Instance instance = parameters[0].getInstance();
                    CommandUtils.checkValueExists(instance, "target instance");
                    return new Variable(Data.getContainer(instance.getId()));
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& int getWaterHeight (Tile tile)
                new Command(TokenConstants.GET_WATER_HEIGHT.getValue(), 1, (self, parameters) -> {
                    Tile tile = parameters[0].getTile();
                    CommandUtils.checkValueExists(tile, "target tile");
                    return new Variable(tile.getWaterHeight());
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& boolean hasEffect (Instance target, Container effectContainer)
                new Command(TokenConstants.HAS_EFFECT.getValue(), 1, (self, parameters) -> {
                    Instance target = parameters[0].getInstance();
                    CommandUtils.checkValueExists(target, "target instance");
                    int containerID = parameters.length >= 2 ? CommandUtils.getAndCheckContainerID(parameters[1]) : -1;

                    if (target.getEffects() != null) {
                        for (Instance effect : target.getEffects()) {
                            if ((containerID == -1) || (containerID == effect.getId())) {
                                return new Variable(1);
                            }
                        }
                    }
                    return new Variable(0);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& list<tile> isNeighbor (Tile origin, Tile target)
                new Command(TokenConstants.IS_NEIGHBOUR.getValue(), 1, (self, parameters) -> {
                    Tile origin = parameters[0].getTile();
                    CommandUtils.checkValueExists(origin, "origin tile");
                    Tile target = parameters[1].getTile();
                    CommandUtils.checkValueExists(target, "target tile");

                    if (origin == target) {
                        return new Variable(1);
                    }

                    for (Tile neighbour : Neighbour.getNeighbours(origin)) {
                        if (origin == neighbour) {
                            return new Variable(1);
                        }
                    }

                    return new Variable(0);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& boolean isOnFloor(Instance instance)
                new Command(TokenConstants.IS_ON_FLOOR.getValue(), 1, (self, parameters) -> {
                    Instance instance = parameters[0].getInstance();
                    CommandUtils.checkValueExists(instance, "target instance");

                    if (instance.getSuperInstance() != null) {
                        Container superContainer = Data.getContainer(instance.getSuperInstance().getId());
                        if (superContainer != null && superContainer.getType() == DataType.TILE) {
                            return new Variable(1);
                        }
                    }

                    return new Variable(0);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& boolean isSelected(Instance instance)
                new Command(TokenConstants.IS_SELECTED.getValue(), 1, (self, parameters) -> {
                    Instance instance = parameters[0].getInstance();
                    CommandUtils.checkValueExists(instance, "target instance");
                    return new Variable(instance == GameOptions.selectedInstance);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& tile randomTile ()
                new Command(TokenConstants.RANDOM_TILE.getValue(), 0, (self, parameters) -> {
                    Planet planet = Data.getPlanet();
                    if (planet != null) {
                        int f = (int) (Math.random() * 20d);
                        int x = (int) (Math.random() * planet.getSize());
                        int y = (int) (Math.random() * planet.getSize());

                        Tile tile = planet.getFace(f).getTile(x,y);
                        return new Variable(tile);
                    }
                    return new Variable();
                })

        );
    }

}
