package engine.data.proto;

import constants.GameConstants;
import engine.data.attributes.Attribute;
import engine.data.variables.DataType;
import engine.graphics.objects.MeshHub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Data {

	private static Container[] containers;
	private static int containerID;

	private static ProtoAttribute[] attributes;
	private static int attributeID;

	private static HashMap<String, MeshHub> meshHubs;

	public static void initialize() {
		containerID = 0;
		containers = new Container[GameConstants.MAX_CONTAINERS];

		attributeID = 0;
		attributes = new ProtoAttribute[GameConstants.MAX_ATTRIBUTES];

		meshHubs = new HashMap<>(GameConstants.MAX_CONTAINERS);
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

	/**
	 * Could be more efficient with a hash map, but maybe there is no need to waste the memory if ids are only asked for at the start of the program.
	 * @param textID
	 * @return id or -1 if not found
	 */
	public static int getContainerID(String textID) {
		for (int i=0; i<containers.length; i++) {
			Container container = containers[i];
			if (container != null) {
				if (container.getTextID().equals(textID)) {
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
		for (MeshHub meshHub : meshHubs.values()) {
			meshHub.loadMesh();
		}
	}

	// ###################################################################################
	// ################################ Clearing the Data ################################
	// ###################################################################################

	public static void clear() {
		// just not referencing the old data anymore is enough. Thanks java garbage collector!
		initialize();

		// we have to clean up the meshHubs though
		for (MeshHub meshHub : meshHubs.values()) {
			meshHub.cleanUp();
		}
	}
}
