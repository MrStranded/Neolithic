package engine.data.proto;

import constants.PropertyKeys;
import constants.ScriptConstants;
import engine.data.Data;
import engine.data.attributes.Attribute;
import engine.data.attributes.InheritedAttribute;
import engine.data.attributes.PreAttribute;
import engine.data.identifiers.ContainerIdentifier;
import engine.data.scripts.Script;
import engine.data.structures.trees.binary.BinaryTree;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.parser.utils.Logger;
import engine.utils.converters.StringConverter;

import java.util.*;
import java.util.stream.Collectors;

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
				addAttribute(preAttribute.getStage(), new InheritedAttribute(id,
						preAttribute.getValue(),
						preAttribute.getVariation(),
						preAttribute.getVariationProbability()));
			} else {
                Logger.error("Attribute with textID '" + preAttribute.getTextID() + "' is never declared!");
			}
		}
		// free the memory
		preAttributeList = null;
	}

	public void finalizeInheritance() {
		if (hasInherited) { return; }

		List<Container> ancestors = getProperty(null, PropertyKeys.INHERITED_CONTAINERS.key())
				.map(Variable::getContainerList)
				.orElse(Collections.emptyList());

		for (Container container : ancestors) {
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

					// own attribute overrides attribute from ancestor
					if (getAttribute(stage, attribute.getId()) == null) {
						addAttribute(stage, attribute);
					}
				});
			}
		}
	}

	private void inheritScripts(Container container) {
		for (String stage : container.getStages()) {
			container.getScripts(stage).forEach(script -> {
				// own script overrides script from ancestor
				if (getScriptStrict(stage, script.getTextId()) == null) {
					addScript(stage, script);
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

	public void setProperty(String stage, String key, Variable value) {
		getStage(stage).set(key, value);
	}

	public Optional<Variable> getProperty(String stage, final String key) {
		return getStage(stage).get(key)
				.or(() -> getDefaultStage().get(key));
	}
	public Optional<Variable> getPropertyStrict(String stage, String key) {
		return getStage(stage).get(key);
	}

	public List<Variable> getOrCreatePropertyList(String stage, String key) {
		// get
		Optional<Variable> list = getStage(stage).get(key);
		if (list.isPresent() && list.get().getType() == DataType.LIST) { return list.get().getList(); }

		// or create
		List<Variable> newList = new ArrayList<>();
		getStage(stage).set(key, new Variable(newList));
		return newList;
	}
	public void mergePropertyList(String stage, String key, Container source) {
		if (source == null) { return; }

		source.getPropertyStrict(stage, key)
				.filter(variable -> variable.getType() == DataType.LIST)
				.ifPresent(variable -> getOrCreatePropertyList(stage, key).addAll(variable.getList())
		);
	}

	public BinaryTree<Attribute> getAttributes(String stage) {
		BinaryTree<Attribute> result = getPropertyStrict(stage, PropertyKeys.ATTRIBUTES.key()).map(Variable::getBinaryTree).orElse(null);
		if (result == null) {
			result = new BinaryTree<>();
			getStage(stage).set(PropertyKeys.ATTRIBUTES.key(), new Variable(result));
		}
		return result;
	}
	public Attribute getAttribute(String stage, int attributeID) {
		Attribute result = getAttributes(stage).get(attributeID);
		if (result == null) { result = getAttributes(null).get(attributeID); }
		return result;
	}
	public int getAttributeValue(String stage, int attributeID) {
		Attribute attribute = getAttribute(stage, attributeID);
		return attribute != null ? attribute.getValue() : 0;
	}
	public void addAttribute(String stage, Attribute attribute) {
		if (attribute == null) { return; }
		BinaryTree<Attribute> attributes = getAttributes(stage);
		attributes.insert(attribute);
	}

	public BinaryTree<Script> getScripts(String stage) {
		BinaryTree<Script> result = getPropertyStrict(stage, PropertyKeys.SCRIPTS.key()).map(Variable::getBinaryTree).orElse(null);
		if (result == null) {
			result = new BinaryTree<>();
			getStage(stage).set(PropertyKeys.SCRIPTS.key(), new Variable(result));
		}
		return result;
	}
	public Script getScript(String stage, String textID) {
		int id = StringConverter.toID(textID);
		Script result = getScripts(stage).get(id);
		if (result == null) { result = getScripts(null).get(id); }
		return result;
	}
	public Script getScriptStrict(String stage, String textID) {
		int id = StringConverter.toID(textID);
		return getScripts(stage).get(id);
	}
	public void addScript(String stage, Script script) {
		if (script == null) { return; }
		BinaryTree<Script> scripts = getScripts(stage);
		scripts.insert(script);
	}

	public void setInheritedContainers(List<ContainerIdentifier> inheritedContainers) {
		getDefaultStage().set(
				PropertyKeys.INHERITED_CONTAINERS.key(),
				new Variable(inheritedContainers.stream().map(Variable::new).collect(Collectors.toList()))
		);
	}

	// ###################################################################################
	// ################################ Accessor Wrappers ################################
	// ###################################################################################

	public String getName(String stage) {
		return getProperty(stage, PropertyKeys.NAME.key()).map(Variable::getString).orElse(textID);
	}

	public String getMeshPath(String stage) {
		return getProperty(stage, PropertyKeys.MESH.key()).map(Variable::getString).orElse(null);
	}

	public double getOpacity(String stage) {
		return getProperty(stage, PropertyKeys.OPACITY.key()).map(Variable::getDouble).orElse(1d);
	}

	public boolean isRunTickScripts(String stage) {
		return getProperty(stage, PropertyKeys.RUN_TICK_SCRIPT.key()).map(Variable::getBoolean).orElse(true);
	}

	// ###################################################################################
	// ################################ Debugging ########################################
	// ###################################################################################

	@Override
	public String toString() {
		return getName(null);
	}

	public void printProperties() {
		stages.keySet().forEach(stage -> {
			Logger.raw("   Stage: " + stage);
			getStage(stage).printProperties("      ");
		});
	}

}
