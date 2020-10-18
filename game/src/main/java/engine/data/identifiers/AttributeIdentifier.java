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
	 * Retrieves the corresponding Attribute from the given instance.
	 * The first time this is called, the textID is being replaced by a numerical id and the textID is discarded.
	 * @return the attribute, stored in the instance, specified by this identifier or null if no such container exists
	 */
	public Attribute retrieve(Instance instance) {
		if (instance == null) {
			return null;
		}
		checkID();
		return instance.getAttribute(id);
	}

	/**
	 * Retrieves the corresponding Attribute from the given instance.
	 * The first time this is called, the textID is being replaced by a numerical id and the textID is discarded.
	 * @return the attribute, stored in the instance, specified by this identifier or null if no such container exists
	 */
	public Attribute retrieve(Container container) {
		if (container == null) {
			return null;
		}
		checkID();
		return container.getAttribute(null, id);
	}

	/**
	 * Retrieves the complete value of the corresponding attribute.
	 * This means the added value of the personal, as well as the species attribute of the specified type.
	 * @param instance to search in
	 * @return personal + species attribute value
	 */
	public int retrieveAll(Instance instance) {
		if (instance == null) {
			return 0;
		}
		checkID();
		return instance.getAttributeValue(id);
	}

	private void checkID() {
		if (id < 0) {
			int attributeID = Data.getProtoAttributeID(textID);
			if (attributeID >= 0) {
				id = attributeID;
				textID = null; // free the space up. thanks java garbage collector! <3
			}
		}
	}

	public String toString() {
		if (textID != null) {
			return textID;
		} else {
			return String.valueOf(id);
		}
	}

}
