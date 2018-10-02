package engine.data.identifiers;

import engine.data.Data;
import engine.data.proto.Container;

public class ContainerIdentifier {

	private String textID;
	private int id = -1;

	public ContainerIdentifier(String textID) {
		this.textID = textID;
	}

	/**
	 * Retrieves the corresponding Container from the game data.
	 * The first time this is called, the textID is being replaced by a numerical id and the textID is discarded.
	 * @return the container specified by this identifier or null if no such container exists
	 */
	public Container retrieve() {
		if (id < 0) {
			int containerID = Data.getContainerID(textID);
			if (containerID >= 0) {
				id = containerID;
				textID = null; // free the space up. thanks java garbage collector! <3
			}
		}
		return Data.getContainer(id);
	}

	public boolean identifies(Container container) {
		if (textID != null) {
			return textID.equals(container.getTextID());
		}
		return Data.getContainer(id) == container;
	}

	public String toString() {
		if (textID != null) {
			return textID;
		} else {
			return String.valueOf(id);
		}
	}

}
