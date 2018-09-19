package engine.data.identifiers;

import engine.data.Data;
import engine.data.attributes.Attribute;
import engine.data.entities.Instance;
import engine.data.proto.Container;

public class AttributeIdentifier {

	private String textID;
	private int id = -1;

	public AttributeIdentifier(String textID) {
		this.textID = textID;
	}

	/**
	 * Retrieves the corresponding Container from the game data.
	 * The first time this is called, the textID is being replaced by a numerical id and the textID is discarded.
	 * @return the container specified by this identifier or null if no such container exists
	 */
	public int retrieve(Instance instance) {
		if (instance == null) {
			return 0;
		}
		if (id < 0) {
			int attributeID = Data.getProtoAttributeID(textID);
			if (attributeID >= 0) {
				id = attributeID;
				textID = null; // free the space up. thanks java garbage collector! <3
			}
		}
		return instance.getAttribute(id);
	}

	public String toString() {
		if (textID != null) {
			return textID;
		} else {
			return String.valueOf(id);
		}
	}

}
