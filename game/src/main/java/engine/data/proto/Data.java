package engine.data.proto;

import engine.data.attributes.Attribute;

public class Data {

	private static Container[] containers;
	private static int containerID = 0;

	public static void initialize() {
		// this is all mock up
		containers = new Container[3];
		containers[0] = new Container("tree");
		containers[1] = new Container("rock");
		containers[2] = new Container("dove");

		Attribute attLife = new Attribute(0);
	}

	public static Container get(int id) {
		if (id >= 0 && id < containers.length) {
			return containers[id];
		}
		return null;
	}

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
