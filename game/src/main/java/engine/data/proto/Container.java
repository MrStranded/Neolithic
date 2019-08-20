package engine.data.proto;

import engine.data.Data;
import engine.data.IDInterface;
import engine.data.attributes.Attribute;
import engine.data.attributes.PreAttribute;
import engine.data.Script;
import engine.data.identifiers.ContainerIdentifier;
import engine.data.structures.trees.binary.BinaryTree;
import engine.data.variables.DataType;
import engine.graphics.objects.MeshHub;
import engine.parser.utils.Logger;
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
	private List<ContainerIdentifier> inheritedContainers;
	private boolean hasInherited = false;

	public Container(String textID, DataType type) {
		this.textID = textID;
		this.type = type;
		attributes = new BinaryTree<>();
		scripts = new BinaryTree<>();

		preAttributeList = new ArrayList<>(8);
		inheritedContainers = new ArrayList<>(0);
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
			} else {
                Logger.error("Attribute with textID '" + preAttribute.getTextID() + "' is never declared!");
			}
		}
		// free the memory
		preAttributeList.clear();
		preAttributeList = null;
	}

	public void finalizeScripts() {
		// maybe unnecessary -> delete?
	}

	public void finalizeInheritance() {
		if (hasInherited) { return; }

		for (ContainerIdentifier containerIdentifier : inheritedContainers) {
			Container container = containerIdentifier.retrieve();
			if (container != null) {
				container.finalizeInheritance();

				inheritAttributes(container);
				inheritScripts(container);
				inheritBehaviour(container);
			}
		}

		hasInherited = true;
	}

	private void inheritAttributes(Container container) {
		for (IDInterface attributeID : container.getAttributes()) {
			addAttribute((Attribute) attributeID);
		}
	}

	private void inheritScripts(Container container) {
		for (IDInterface scriptID : container.getScripts()) {
			Script script = (Script) scriptID;
			if (script != null) {
				Script previous = getScript(script.getTextId());
				if (previous == null) {
					addScript(script);
				}
			}
		}
	}

	protected void inheritBehaviour(Container container) {
		// is overwritten in CreatureContainer
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

	public Attribute getAttribute(int attributeID) {
		return attributes.get(attributeID);
	}

	public int getAttributeValue(int attributeID) {
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

	public void addInheritance(String textID) {
		inheritedContainers.add(new ContainerIdentifier(textID));
	}
	public List<ContainerIdentifier> getInheritedContainers() {
		return inheritedContainers;
	}
	public void setInheritedContainers(List<ContainerIdentifier> inheritedContainers) {
		this.inheritedContainers = inheritedContainers;
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
