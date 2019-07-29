package engine.data;

import constants.GameConstants;
import engine.data.entities.Instance;
import engine.data.planetary.Planet;
import engine.data.proto.Container;
import engine.data.proto.ProtoAttribute;
import engine.data.variables.DataType;
import engine.graphics.objects.MeshHub;

import java.util.*;

public class Data {

	private static Planet planet;

	private static Container[] containers;
	private static int containerID;

	private static ProtoAttribute[] attributes;
	private static int attributeID;

	private static HashMap<String, MeshHub> meshHubs;

	private static Queue<Instance> instanceQueue;

	// ###################################################################################
	// ################################ Initialization ###################################
	// ###################################################################################

	public static void initialize() {
		containerID = 0;
		containers = new Container[GameConstants.MAX_CONTAINERS];

		attributeID = 0;
		attributes = new ProtoAttribute[GameConstants.MAX_ATTRIBUTES];

		meshHubs = new HashMap<>(GameConstants.MAX_CONTAINERS);

		instanceQueue = new LinkedList<>();
	}

	// ###################################################################################
	// ################################ Retrieval ########################################
	// ###################################################################################

	public static Container getContainer(int id) {
		if (id >= 0 && id < containerID) {
			return containers[id];
		}
		return null;
	}

	public static Container getContainer(String textID) {
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
		for (int i=0; i<attributes.length; i++) {
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
		if (instanceQueue.isEmpty()) {
			return null;
		}
		return instanceQueue.poll();
	}

	/**
	 * Returns the MeshHub with the specified path or null if none is found.
	 * @param path of the mesh
	 * @return MeshHub if existant or null otherwise
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
		if (containerID >= GameConstants.MAX_CONTAINERS || container == null) {
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
	 * Adds the given creature to the end of the creature queue. Does nothing if the given creature is null.
	 * @param instance to add to queue
	 */
	public static void addInstanceToQueue(Instance instance) {
		if (instance != null) {
			instanceQueue.add(instance);
		}
	}

	// ###################################################################################
	// ################################ Preparing for Game ###############################
	// ###################################################################################

	public static void finalizeIDs() {
		for (Container container : containers) {
			if (container != null) {
				container.finalizeAttributes();
				container.finalizeScripts();
			}
		}
	}

	/**
	 * Loads the missing data that has been specified by the parser.
	 * This includes meshes.
	 */
	public static void load() {
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
		for (Instance instance : instanceQueue) {
			instance.actualizeObjectPosition();
		}
	}

    /**
     * This method shuffles the positions of the instances in the instance queue.
     */
    public static void shuffleInstanceQueue() {
        List<Instance> list = (List<Instance>) instanceQueue;
        Collections.shuffle(list);
        instanceQueue = new LinkedList<>();
        instanceQueue.addAll(list);
    }

	// ###################################################################################
	// ################################ Clearing the Data ################################
	// ###################################################################################

	public static void clear() {
		// we have to clean up the meshHubs though
		if (meshHubs != null) {
			for (MeshHub meshHub : meshHubs.values()) {
				meshHub.cleanUp();
			}
		}

		// just not referencing the old data anymore is enough. Thanks java garbage collector!
		initialize();
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public static Planet getPlanet() {
		return planet;
	}
	public static void setPlanet(Planet planet) {
		Data.planet = planet;
	}

}
