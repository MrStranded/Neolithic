package engine.data.proto;

import constants.GameConstants;
import engine.data.attributes.Attribute;

public class Data {

	private static Container[] containers;
	private static int containerID = 0;

	public static void initialize() {
		containers = new Container[GameConstants.MAX_CONTAINERS];
	}

	public static Container get(int id) {
		if (id >= 0 && id < containers.length) {
			return containers[id];
		}
		return null;
	}

	/**
	 * Could be more efficient with a hash map, but maybe there is no need to waste the memory if ids are only asked for at the start of the program.
	 * @param textID
	 * @return
	 */
	public static int getID(String textID) {
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
}
