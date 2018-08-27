package engine.data.proto;

import constants.GameConstants;
import engine.data.attributes.Attribute;
import engine.data.variables.DataType;

import java.util.ArrayList;
import java.util.List;

public class Data {

	private static Container[] containers;
	private static int containerID = 0;

	private static ProtoAttribute[] attributes;
	private static int attributeID = 0;

	public static void initialize() {
		containers = new Container[GameConstants.MAX_CONTAINERS];
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
	 * @return
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
}
