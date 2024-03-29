package engine.data;

import constants.GameConstants;
import constants.ScriptConstants;
import engine.data.entities.Instance;
import engine.data.entities.Tile;
import engine.data.planetary.Planet;
import engine.data.proto.Container;
import engine.data.proto.ProtoAttribute;
import engine.data.scripts.ScriptRun;
import engine.data.variables.DataType;
import engine.graphics.gui.GUIInterface;
import engine.graphics.objects.GraphicalObject;
import engine.graphics.objects.MeshHub;
import engine.graphics.objects.planet.Sun;
import engine.parser.utils.Logger;
import load.ModOrderLoader;
import load.TextureLoader;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class Data {

	private static GUIInterface hud;

	private static Planet planet;
	private static Sun sun;
	private static GraphicalObject moon;

	private static Container mainContainer;
	private static Instance mainInstance;

	private static Container[] containers;
	private static int containerID;

	private static ProtoAttribute[] attributes;
	private static int attributeID;

	private static HashMap<String, MeshHub> meshHubs;

	private static HashMap<Integer, String> idToTextId;

	private static ConcurrentLinkedQueue<Instance> instanceQueue;
	private static Queue<ScriptRun> scriptRuns;
	private static boolean updatePlanetMesh = true;

	// ###################################################################################
	// ################################ Initialization ###################################
	// ###################################################################################

	public static void initialize() {
		prepareInstances();
		prepareProto();
		prepareMainInstance(prepareMainContainer());
	}

	public static void initializeReload() {
		idToTextId = new HashMap<>((int) (instanceQueue.size() * 1.5));
		instanceQueue.forEach(instance -> {
			if (instance.getId() >= 0) {
				Optional<Container> container = getContainer(instance.getId());

				container.map(Container::getTextID).ifPresent(textId -> {
					if (ScriptConstants.MAIN_CONTAINER.equals(textId)) {
						Logger.trace("PRE - MAIN CONTAINER " + instance.getId());
					}
					idToTextId.put(instance.getId(), textId);
				});
			}
		});

		prepareProto();
		prepareMainContainer();
	}

	public static void finishReload() {
		// set ids of instances to correct new values
		instanceQueue.forEach(instance -> {
			if (instance.getId() >= 0) {
				String textId = idToTextId.get(instance.getId());

				if (ScriptConstants.MAIN_CONTAINER.equals(textId)) {
					Logger.trace("POST - MAIN CONTAINER " + instance.getId());
				}

				int id = Data.getContainerID(textId);

				if (id >= 0) {
					instance.setId(id);
				} else {
					Logger.error("Could not reassign ID to instance during reload. TextID: " + textId);
				}
			}
		});
	}

	private static void prepareInstances() {
		instanceQueue = new ConcurrentLinkedQueue<>();
		scriptRuns = new LinkedList<>();
	}

	/**
	 * Proto includes containers, attributes and mesh hubs
	 */
	private static void prepareProto() {
		containerID = 0;
		containers = new Container[GameConstants.MAX_CONTAINERS];

		attributeID = 0;
		attributes = new ProtoAttribute[GameConstants.MAX_ATTRIBUTES];

		meshHubs = new HashMap<>(GameConstants.MAX_CONTAINERS);
	}

	/**
	 * The mainContainer container contains global scripts
	 */
	private static int prepareMainContainer() {
		mainContainer = new Container(ScriptConstants.MAIN_CONTAINER, DataType.CONTAINER);
		return addContainer(mainContainer);
	}
	private static void prepareMainInstance(int mainID) {
		mainInstance = Data.addInstanceToQueue(new Instance(mainID));
	}

	// ###################################################################################
	// ################################ Retrieval ########################################
	// ###################################################################################

	public static Optional<Container> getContainer(int id) {
		if (id >= 0 && id < containerID) {
			return Optional.of(containers[id]);
		}
		return Optional.empty();
	}

	public static Optional<Container> getContainer(String textID) {
		return getContainer(getContainerID(textID));
	}

	/**
	 * Could be more efficient with a hash map, but maybe there is no need to waste the memory if ids are only asked for at the start of the program.
	 * @param textID
	 * @return id or -1 if not found
	 */
	public static int getContainerID(String textID) {
		for (int i=0; i<containers.length; i++) {
			if (containers[i] != null) {
				if (containers[i].getTextID().equals(textID)) {
					return i;
				}
			}
		}
		return -1;
	}

	public static ProtoAttribute getProtoAttribute(int id) {
		if (id >= 0 && id < attributeID) {
			return attributes[id];
		}
		return null;
	}

	/**
	 * Returns the id of the requested textID. Returns -1 if not found.
	 * @param textID to search for
	 * @return id or -1 if not found
	 */
	public static int getProtoAttributeID(String textID) {
		for (int i = 0; i < attributes.length; i++) {
			ProtoAttribute protoAttribute = attributes[i];
			if (protoAttribute != null) {
				if (protoAttribute.getTextID().equals(textID)) {
					return i;
				}
			}
		}
		return -1;
	}

    /**
     * Returns a list of all attribute ids that are registered in the game.
     * @return list of protoattribute ids
     */
	public static List<Integer> getAllAttributeIDs() {
		List<Integer> attributeIDList = new ArrayList<>(8);

		for (int i = 0; i < attributes.length; i++) {
			if (attributes[i] != null) {
				attributeIDList.add(i);
			}
		}

		return attributeIDList;
	}

	/**
	 * Returns all instances from the queue with the given container ID.
	 * @param id of container
	 * @return list of instances of given type
	 */
	public static List<Instance> getAllInstancesWithID(int id) {
		List<Instance> list = new ArrayList<>();

		try {
			list.addAll(
					instanceQueue.stream()
							.filter(i -> i.getId() == id)
							.collect(Collectors.toList())
			);
		} catch (Exception e) {
			Logger.error("Concurrent modification during 'Data.getAllInstancesWithID()'");
		}

		return list;
	}

	/**
	 * Creates a list with all the containers of a given DataType.
	 * @param type
	 * @return
	 */
	public static List<Container> getContainersOfType(DataType type) {
		List<Container> containerList = new ArrayList<>(8);

		for (Container container : containers) {
			if (container != null) {
				if (container.getType() == type) {
					containerList.add(container);
				}
			}
		}

		return containerList;
	}

	/**
	 * Returns the next creature from the creature queue and removes it from there.
	 * If there is no next create, null is returned.
	 * @return next creature in queue
	 */
	public static Instance getNextInstance() {
		return instanceQueue.poll();
	}

	/**
	 * Returns the MeshHub with the specified path or null if none is found.
	 * @param path of the mesh
	 * @return MeshHub if existent or null otherwise
	 */
	public static MeshHub getMeshHub(String path) {
		return meshHubs.get(path);
	}

	/**
	 * Returns a Collection of all the MeshHubs to iterate over.
	 * @return Collection of all stored MeshHubs
	 */
	public static Collection<MeshHub> getMeshHubs() {
		return meshHubs.values();
	}

	// ###################################################################################
	// ################################ Feeding ##########################################
	// ###################################################################################

	public static int addProtoAttribute(ProtoAttribute protoAttribute) {
		if (attributeID >= GameConstants.MAX_ATTRIBUTES || protoAttribute == null) {
			return -1;
		}
		int id = attributeID;
		attributes[id] = protoAttribute;
		attributeID++;
		return id;
	}

	public static int addContainer(Container container) {
		if (containerID >= GameConstants.MAX_CONTAINERS) {
			Logger.error("More Containers than allowed! Increase MAX_CONTAINERS. (currently MAX_CONTAINERS = " + GameConstants.MAX_CONTAINERS + ")");
			return -1;
		}
		if (container == null) {
			Logger.error("container == null in Data.addContainer!");
			return -1;
		}
		int id = containerID;
		containers[id] = container;
		containerID++;
		return id;
	}

	/**
	 * Creates and stores a MeshHub with the specified path.
	 * If such a MeshHub already exists, nothing is done.
	 * The Method returns the (possibly freshly created) MeshHub.
	 * @param path of the mesh
	 * @return MeshHub with specified path
	 */
	public static MeshHub addMeshHub(String path) {
		MeshHub meshHub = getMeshHub(path);
		if (meshHub == null) {
			meshHub = new MeshHub(path);
			meshHubs.put(path, meshHub);
		}
		return meshHub;
	}

	/**
	 * Adds the given instance to the end of the instance queue. Does nothing if the given instance is null.
	 * @param instance to add to queue
	 * @return given instance
	 */
	public static Instance addInstanceToQueue(Instance instance) {
		if (instance != null) {
			instanceQueue.add(instance);
		}
		return instance;
	}

	public static void addScriptRun(ScriptRun scriptRun) {
		scriptRuns.add(scriptRun);
	}

	// ###################################################################################
	// ################################ Preparing for Game ###############################
	// ###################################################################################

	public static void prepareForGame() {
		for (Container container : containers) {
			if (container != null) {
				container.finalizeAttributes();
			}
		}
		for (Container container : containers) {
			if (container != null) {
				container.finalizeInheritance();
			}
		}
	}

	public static void loadMeshHubs() {
		if (meshHubs != null) {
			for (MeshHub meshHub : meshHubs.values()) {
				meshHub.loadMesh();
			}
		}
	}

	/**
	 * This method actualizes the positions of all the instances on the planet with the (possibly) new facePart.getMid()s.
	 */
	public static void updateInstancePositions() {
		instanceQueue.forEach(instance -> {
			Tile tile = instance.getPosition();
			if (tile != null && tile.getTileMesh().hasChanged()) {

				Optional<Container> container = Data.getContainer(instance.getId());
				container.filter(c -> c.getType() != DataType.TILE)
						.ifPresent(c -> instance.actualizeObjectPosition());
			}
		});
	}

    /**
     * This method shuffles the positions of the instances in the instance queue.
     */
    public static void shuffleInstanceQueue() {
    	List<Instance> list = new ArrayList<>(instanceQueue);
        Collections.shuffle(list);
        instanceQueue.clear();
        instanceQueue.addAll(list);
    }

	// ###################################################################################
	// ################################ Clearing the Data ################################
	// ###################################################################################

	public static void clear() {
		// we have to clean up the meshHubs though
		clearMeshHubs();

		// emptying caches
		TextureLoader.clear();
		ModOrderLoader.clear();

		// for testing:
		// just not referencing the old data anymore is enough. Thanks java garbage collector!
		initialize();
	}

	public static void clearMeshHubs() {
    	instanceQueue.forEach(Instance::clearMeshHub);

		if (meshHubs != null) {
			for (MeshHub meshHub : meshHubs.values()) {
				meshHub.cleanUp();
			}
		}
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public static Queue<Instance> getInstanceQueue() { return instanceQueue; }

	public static boolean shouldUpdatePlanetMesh() {
		return updatePlanetMesh;
	}

	public static void setUpdatePlanetMesh(boolean updatePlanetMesh) {
		Data.updatePlanetMesh = updatePlanetMesh;
	}

	public static Queue<ScriptRun> getScriptRuns() {
    	return scriptRuns;
	}

	public static Container getMainContainer() {
        return mainContainer;
    }
    public static Instance getMainInstance() { return mainInstance; }

    public static Planet getPlanet() {
		return planet;
	}
	public static void setPlanet(Planet planet) {
		Data.planet = planet;
	}

    public static Sun getSun() {
        return sun;
    }
    public static void setSun(Sun sun) {
        Data.sun = sun;
    }

    public static GraphicalObject getMoon() {
        return moon;
    }
    public static void setMoon(GraphicalObject moon) {
        Data.moon = moon;
    }

	public static GUIInterface getHud() {
		return hud;
	}
	public static void setHud(GUIInterface hud) {
		Data.hud = hud;
	}
}
