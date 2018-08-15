package engine.data.proto;

import engine.data.attributes.Effect;
import engine.graphics.objects.models.Mesh;

/**
 * The container class holds values that apply to several instances of a certain type (defined by their id).
 * For example: Items with the same id also share the name and the mesh, which are stored in a Container.
 */
public class Container {

	// classification
	private int id;
	private String textID;

	// graphical
	private Mesh mesh = null;

	// game logic
	private String name = "[NAME]";
	private Effect commonEffect = null;

	public Container(int id, String textID) {
		this.id = id;
		this.textID = textID;
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public int getId() {
		return id;
	}

	public String getTextID() {
		return textID;
	}
}
