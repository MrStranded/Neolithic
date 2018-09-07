package engine.data.proto;

import engine.data.Data;
import engine.data.IDInterface;
import engine.data.attributes.Attribute;
import engine.data.attributes.PreAttribute;
import engine.data.structures.Script;
import engine.data.structures.trees.binary.BinaryTree;
import engine.data.variables.DataType;
import engine.graphics.objects.MeshHub;
import engine.utils.converters.StringConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * The container class holds values that apply to several instances of a certain type (defined by their id).
 * For example: Items with the same id also share the (species) name and the mesh, which are stored in a Container.
 */
public class Container {

	// classification
	private String textID;
	private DataType type;

	// graphical
	private MeshHub meshHub = null;

	// game logic
	private String name = "[NAME]";
	private BinaryTree<Attribute> attributes;
	private BinaryTree<Script> scripts;

	// loading process
	private List<PreAttribute> preAttributeList;

	public Container(String textID, DataType type) {
		this.textID = textID;
		this.type = type;
		attributes = new BinaryTree<>();
		scripts = new BinaryTree<>();

		preAttributeList = new ArrayList<>(8);
	}

	// ###################################################################################
	// ################################ Preparing for Game ###############################
	// ###################################################################################

	public void finalizeAttributes() {
		// convert
		for (PreAttribute preAttribute : preAttributeList) {
			int id = Data.getProtoAttributeID(preAttribute.getTextID());
			if (id >= 0) {
				attributes.insert(new Attribute(id, preAttribute.getValue()));
			}
		}
		// free the memory
		preAttributeList.clear();
		preAttributeList = null;
	}

	public void finalizeScripts() {
		// maybe unnecessary -> delete?
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

	public void addPreAttribute(PreAttribute preAttribute) {
		if (preAttribute != null) {
			preAttributeList.add(preAttribute);
		}
	}

	public void addScript(Script script) {
		if (script != null) {
			scripts.insert(script);
		}
	}
	public Script getScript(String textID) {
		if (scripts != null) {
			return scripts.get(StringConverter.toID(textID));
		}
		return null;
	}

	public MeshHub getMeshHub() {
		return meshHub;
	}
	public void setMeshHub(MeshHub meshHub) {
		this.meshHub = meshHub;
	}

	public IDInterface[] getScripts() {
		return scripts.toArray();
	}
}
