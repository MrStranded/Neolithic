package engine.data.proto;

import constants.TopologyConstants;
import engine.data.IDInterface;
import engine.data.attributes.Attribute;
import engine.data.structures.Script;
import engine.data.structures.trees.binary.BinaryTree;
import engine.data.variables.DataType;
import engine.graphics.objects.models.Mesh;
import engine.graphics.renderer.color.RGBA;

/**
 * The container class holds values that apply to several instances of a certain type (defined by their id).
 * For example: Items with the same id also share the (species) name and the mesh, which are stored in a Container.
 */
public class Container {

	// classification
	private String textID;
	private DataType type;

	// graphical
	private Mesh mesh = null;

	// tile specific
	private int preferredHeight = 0;
	private RGBA color = TopologyConstants.TILE_DEFAULT_COLOR;

	// game logic
	private String name = "[NAME]";
	private BinaryTree<Attribute> attributes;
	private BinaryTree<Script> scripts;

	public Container(String textID, DataType type) {
		this.textID = textID;
		this.type = type;
		attributes = new BinaryTree<>();
		scripts = new BinaryTree<>();
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public String getTextID() {
		return textID;
	}

	public DataType getType() {
		return type;
	}

	public RGBA getColor() {
		return color;
	}
	public void setColor(RGBA color) {
		this.color = color;
	}

	public int getPreferredHeight() {
		return preferredHeight;
	}
	public void setPreferredHeight(int preferredHeight) {
		this.preferredHeight = preferredHeight;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int getAttribute(int attributeID) {
		Attribute attribute = attributes.get(attributeID);
		return attribute != null? attribute.getValue() : 0;
	}

	public IDInterface[] getAttributes() {
		return attributes.toArray();
	}

	public void addAttribute(Attribute attribute) {
		if (attribute != null) {
			attributes.insert(attribute);
		}
	}
}
