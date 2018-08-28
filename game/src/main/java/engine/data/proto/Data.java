package engine.data.proto;

import constants.GameConstants;
import engine.data.attributes.Attribute;
import engine.data.variables.DataType;

import java.util.ArrayList;
import java.util.List;

public class Data {

	private static Container[] containers;
	private static int containerID;

	private static ProtoAttribute[] attributes;
	private static int attributeID;

	public static void initialize() {
		containerID = 0;
		containers = new Container[GameConstants.MAX_CONTAINERS];

		attributeID = 0;
		attributes = new ProtoAttribute[GameConstants.MAX_ATTRIBUTES];
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

	// ###################################################################################
	// ################################ Preparing for Game ###############################
	// ###################################################################################

	public static void finalizeIDs() {
		for (Container container : containers) {
			if (container != null) {
				container.finalizeAttributes();
				container.finalizeScripts();

				if (container.getType() == DataType.CREATURE) {
					((CreatureContainer) container).finalizeBehaviour();
				} else if (container.getType() == DataType.DRIVE) {
					((DriveContainer) container).finalizeSolutions();
				}
			}
		}
	}

	// ###################################################################################
	// ################################ Clearing the Data ################################
	// ###################################################################################

	public static void clear() {
		// just not referencing the old data anymore is enough. Thanks java garbage collector!
		initialize();
	}
}
