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

import java.util.*;

/**
 * The container class holds values that apply to several instances of a certain type (defined by their id).
 * For example: Items with the same id also share the (species) name and the mesh, which are stored in a Container.
 */
public class Container {

	// classification
	private String textID;
	private DataType type;

	// stages
	private Map<String, StageScope> stages;

	// loading process
	private List<PreAttribute> preAttributeList;
	private boolean hasInherited = false;

	public Container(String textID, DataType type) {
		this.textID = textID;
		this.type = type;

		preAttributeList = new ArrayList<>(8);

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
				getAttributes(preAttribute.getStage()).insert(
						new Attribute(id, preAttribute.getValue(), preAttribute.getVariation())
				);
			} else {
                Logger.error("Attribute with textID '" + preAttribute.getTextID() + "' is never declared!");
			}
		}
		// free the memory
		preAttributeList = null;
	}

	public void finalizeInheritance() {
		if (hasInherited) { return; }

		for (ContainerIdentifier containerIdentifier
				: getPropertyList(null, ScriptConstants.KEY_INHERITED_CONATINERS).orElse(Collections.emptyList())) {
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
		for (String stage : container.getStages()) {
			BinaryTree<Attribute> tree = container.getAttributes(stage);
			if (tree != null) {
				tree.forEach(attribute -> {
					addAttribute(stage, attribute);
				});
			}
		}
	}

	private void inheritScripts(Container container) {
		for (String stage : container.getStages()) {
			BinaryTree<Script> tree = container.getScripts(stage);
			if (tree != null) {
				tree.forEach(script -> {
					if (script != null) {
						if (getScript(stage, script.getTextId()) == null) {
							addScript(stage, script);
						}
					}
				});
			}
		}
	}

	protected void inheritBehaviour(Container container) {
		// is overwritten in CreatureContainer
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public void addPreAttribute(PreAttribute preAttribute) {
		if (preAttribute != null) {
			preAttributeList.add(preAttribute);
		}
	}

	public String getTextID() {
		return textID;
	}

	public DataType getType() {
		return type;
	}

	// ###################################################################################
	// ################################ Stages & Properties ##############################
	// ###################################################################################

	public Set<String> getStages() {
		return stages.keySet();
	}

	protected StageScope getDefaultStage() {
		return stages.computeIfAbsent(ScriptConstants.DEFAULT_STAGE, key -> new StageScope());
	}

	/**
	 * Retrieves specified stage scope if present.<br>
	 * If it is not present, but the given stage key is not null, a new stage scope with the given stage key is created.<br>
	 * If the given stage key is null, the default stage scope is retrieved.
	 * @param stage key
	 * @return stage scope with specified stage key
	 */
	private StageScope getStage(String stage) {
		if (stage == null) { stage = ScriptConstants.DEFAULT_STAGE; }
		return stages.computeIfAbsent(stage, key -> new StageScope());
	}

	public void setProperty(String stage, String key, Object value) {
		getStage(stage).set(key, value);
	}

	public Optional<String> getPropertyString(String stage, String key) {
		return getStage(stage).getString(key)
				.or(() -> getDefaultStage().getString(key));
	}
	public Optional<Boolean> getPropertyBoolean(String stage, String key) {
		return getStage(stage).getBoolean(key)
				.or(() -> getDefaultStage().getBoolean(key));
	}
	public Optional<Integer> getPropertyInt(String stage, String key) {
		return getStage(stage).getInt(key)
				.or(() -> getDefaultStage().getInt(key));
	}
	public Optional<Double> getPropertyDouble(String stage, String key) {
		return getStage(stage).getDouble(key)
				.or(() -> getDefaultStage().getDouble(key));
	}
	public Optional<List<ContainerIdentifier>> getPropertyList(String stage, String key) {
		return getStage(stage).getIdList(key)
				.or(() -> getDefaultStage().getIdList(key));
	}
	public List<ContainerIdentifier> getOrCreatePropertyList(String stage, String key) {
		Optional<List<ContainerIdentifier>> list = getStage(stage).getIdList(key)
				.or(() -> getDefaultStage().getIdList(key));

		if (list.isPresent()) { return list.get(); }

		List<ContainerIdentifier> l = new ArrayList<ContainerIdentifier>(4);
		getStage(stage).set(key, l);
		return l;
	}

	public BinaryTree<Attribute> getAttributes(String stage) {
		BinaryTree<Attribute> result = getStage(stage).getAttributes();
		if (result == null) { result = getDefaultStage().getAttributes(); }
		if (result == null) {
			result = new BinaryTree<>();
			getStage(null).set(ScriptConstants.KEY_ATTRIBUTES, result);
		}
		return result;
	}
	public Attribute getAttribute(String stage, int attributeID) {
		Attribute result = getStage(stage).getAttribute(attributeID);
		if (result == null) { result = getDefaultStage().getAttribute(attributeID); }
		return result;
	}
	public int getAttributeValue(int attributeID) {
		Attribute attribute = getAttribute(null, attributeID);
		return attribute != null? attribute.getValue() : 0;
	}
	public void addAttribute(String stage, Attribute attribute) {
		BinaryTree<Attribute> attributes = getAttributes(stage);
		if (attribute != null) {
			attributes.insert(attribute);
		}
	}

	public BinaryTree<Script> getScripts(String stage) {
		BinaryTree<Script> result = getStage(stage).getScripts();
		if (result == null) { result = getDefaultStage().getScripts(); }
		if (result == null) {
			result = new BinaryTree<>();
			getStage(stage).set(ScriptConstants.KEY_SCIPTS, result);
		}
		return result;
	}
	public Script getScript(String stage, String textID) {
		int id = StringConverter.toID(textID);
		Script result = getStage(stage).getScript(id);
		if (result == null) { result = getDefaultStage().getScript(id); }
		return result;
	}
	public void addScript(String stage, Script script) {
		if (script != null) {
			getScripts(stage).insert(script);
		}
	}

//	public void addInheritance(String textID) {
//		getDefaultStage().getIdList(ScriptConstants.KEY_INHERITED_CONATINERS).add(new ContainerIdentifier(textID));
//	}
	public List<ContainerIdentifier> getInheritedContainers() {
		return getDefaultStage().getIdList(ScriptConstants.KEY_INHERITED_CONATINERS).orElse(Collections.emptyList());
	}
	public void setInheritedContainers(List<ContainerIdentifier> inheritedContainers) {
		getDefaultStage().set(ScriptConstants.KEY_INHERITED_CONATINERS, inheritedContainers);
	}

	// ###################################################################################
	// ################################ Accessor Wrappers ################################
	// ###################################################################################

	public String getName() {
		return getPropertyString(null, ScriptConstants.KEY_NAME).orElse(textID);
	}

	public String getMeshPath(String stage) {
		return getPropertyString(stage, ScriptConstants.KEY_MESH).orElse(null);
	}

	public double getOpacity() {
		return getPropertyDouble(null, ScriptConstants.KEY_OPACITY).orElse(1d);
	}

	public boolean isRunTickScripts() {
		return getPropertyBoolean(null, ScriptConstants.KEY_RUN_TICK_SCRIPT).orElse(true);
	}
	public void setRunTickScripts(boolean runTickScripts) {
		setProperty(null, ScriptConstants.KEY_RUN_TICK_SCRIPT, runTickScripts);
	}

}
