package engine.data.proto;

import constants.ScriptConstants;
import engine.data.Data;
import engine.data.IDInterface;
import engine.data.attributes.Attribute;
import engine.data.attributes.PreAttribute;
import engine.data.scripts.Script;
import engine.data.identifiers.ContainerIdentifier;
import engine.data.structures.trees.binary.BinaryTree;
import engine.data.variables.DataType;
import engine.graphics.objects.MeshHub;
import engine.parser.utils.Logger;
import engine.utils.converters.StringConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private double opacity = 1.0;

	// game logic
//	private String name;
	private boolean runTickScripts = true;
//	private BinaryTree<Attribute> attributes;
//	private BinaryTree<Script> scripts;

	// stages
	private Map<String, StageScope> stages;

	// loading process
	private List<PreAttribute> preAttributeList;
//	private List<ContainerIdentifier> inheritedContainers;
	private boolean hasInherited = false;

	public Container(String textID, DataType type) {
		this.textID = textID;
		this.type = type;
//		attributes = new BinaryTree<>();
//		scripts = new BinaryTree<>();

		preAttributeList = new ArrayList<>(8);
//		inheritedContainers = new ArrayList<>(0);

		stages = new HashMap<>(8);
	}

	// ###################################################################################
	// ################################ Preparing for Game ###############################
	// ###################################################################################

	public void finalizeAttributes() {
		// convert
		for (PreAttribute preAttribute : preAttributeList) {
			int id = Data.getProtoAttributeID(preAttribute.getTextID());
			if (id >= 0) {
				getDefaultStage().getAttributes().insert(
						new Attribute(id, preAttribute.getValue(), preAttribute.getVariation())
				);
//				attributes.insert(new Attribute(id, preAttribute.getValue(), preAttribute.getVariation()));
			} else {
                Logger.error("Attribute with textID '" + preAttribute.getTextID() + "' is never declared!");
			}
		}
		// free the memory
		//preAttributeList.clear();
		preAttributeList = null;
	}

	public void finalizeScripts() {
		// maybe unnecessary -> delete?
	}

	public void finalizeInheritance() {
		if (hasInherited) { return; }

		for (ContainerIdentifier containerIdentifier : getDefaultStage().getIdList(ScriptConstants.KEY_INHERITED_CONATINERS)) {
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
		BinaryTree<Attribute> tree = container.getAttributes();
		if (tree != null) {
			tree.forEach(attributeIdentifier -> {
				addAttribute((Attribute) attributeIdentifier);
			});
		}
	}

	private void inheritScripts(Container container) {
		BinaryTree<Script> tree = container.getScripts();
		if (tree != null) {
			tree.forEach(scriptIdentifier -> {
				Script script = (Script) scriptIdentifier;
				if (script != null) {
					if (getScript(script.getTextId()) == null) {
						addScript(script);
					}
				}
			});
		}
	}

	protected void inheritBehaviour(Container container) {
		// is overwritten in CreatureContainer
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public StageScope getDefaultStage() {
		return stages.computeIfAbsent(ScriptConstants.DEFAULT_STAGE, key -> new StageScope());
	}

	/**
	 * Retrieves specified stage scope if present.<br>
	 * If it is not present, but the given stage key is not null, a new stage scope with the given stage key is created.<br>
	 * If the given stage key is null, the default stage scope is retrieved.
	 * @param stage key
	 * @return stage scope with specified stage key
	 */
	public StageScope getStage(String stage) {
		if (stage == null) { stage = ScriptConstants.DEFAULT_STAGE; }
		return stages.computeIfAbsent(stage, key -> new StageScope());
	}

	public String getTextID() {
		return textID;
	}

	public DataType getType() {
		return type;
	}

	public String getName() {
		String name = getDefaultStage().getString(ScriptConstants.KEY_NAME);
		if (name == null || "".equals(name)) { return textID; }
		return getDefaultStage().getString(ScriptConstants.KEY_NAME);
	}
	public void setName(String stage, String name) {
//		this.name = name;
		getStage(stage).set(ScriptConstants.KEY_NAME, name);
	}

	public Attribute getAttribute(int attributeID) {
//		return attributes.get(attributeID);
		return getDefaultStage().getAttribute(attributeID);
	}

	public int getAttributeValue(int attributeID) {
		Attribute attribute = getAttribute(attributeID);
		return attribute != null? attribute.getValue() : 0;
	}

	public BinaryTree<Attribute> getAttributes() {
		return getDefaultStage().getAttributes();
//		return attributes;
	}

	public void addAttribute(Attribute attribute) {
		if (attribute != null) {
			getDefaultStage().getAttributes().insert(attribute);
//			attributes.insert(attribute);
		}
	}

	public void addPreAttribute(PreAttribute preAttribute) {
		if (preAttribute != null) {
			preAttributeList.add(preAttribute);
		}
	}

	public void addScript(Script script) {
		if (script != null) {
			getDefaultStage().getScripts().insert(script);
//			scripts.insert(script);
		}
	}
	public Script getScript(String textID) {
		return getDefaultStage().getScripts().get(StringConverter.toID(textID));
//		if (scripts != null) {
//			return scripts.get(StringConverter.toID(textID));
//		}
//		return null;
	}

	public void addInheritance(String textID) {
		getDefaultStage().getIdList(ScriptConstants.KEY_INHERITED_CONATINERS).add(new ContainerIdentifier(textID));
//		inheritedContainers.add(new ContainerIdentifier(textID));
	}
	public List<ContainerIdentifier> getInheritedContainers() {
		return getDefaultStage().getIdList(ScriptConstants.KEY_INHERITED_CONATINERS);
//		return inheritedContainers;
	}
	public void setInheritedContainers(List<ContainerIdentifier> inheritedContainers) {
		getDefaultStage().set(ScriptConstants.KEY_INHERITED_CONATINERS, inheritedContainers);
//		this.inheritedContainers = inheritedContainers;
	}

	public MeshHub getMeshHub() {
		return meshHub;
	}
	public void setMeshHub(MeshHub meshHub) {
		this.meshHub = meshHub;
		if (meshHub != null) { meshHub.setMeshOpacity(opacity); }
	}

	public double getOpacity() {
		return getDefaultStage().getDouble(ScriptConstants.KEY_OPACITY);
//		return opacity;
	}
	public void setOpacity(double opacity) {
		getDefaultStage().set(ScriptConstants.KEY_OPACITY, opacity);
//		this.opacity = opacity;
		if (meshHub != null) { meshHub.setMeshOpacity(opacity); }
	}

	public boolean isRunTickScripts() {
		return runTickScripts;
	}
	public void setRunTickScripts(boolean runTickScripts) {
		this.runTickScripts = runTickScripts;
	}

	public BinaryTree<Script> getScripts() {
//		return scripts;
		return getDefaultStage().getScripts();
	}

}
