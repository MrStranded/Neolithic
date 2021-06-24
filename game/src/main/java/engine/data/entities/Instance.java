package engine.data.entities;

import constants.PropertyKeys;
import constants.ScriptConstants;
import engine.data.Data;
import engine.data.attributes.Attribute;
import engine.data.behaviour.Occupation;
import engine.data.interaction.SelectedInstance;
import engine.data.options.GameOptions;
import engine.data.proto.*;
import engine.data.scripts.Script;
import engine.data.structures.WeightedList;
import engine.data.structures.trees.binary.BinaryTree;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.graphics.objects.MeshHub;
import engine.graphics.objects.movement.MoveableObject;
import engine.logic.topology.GeographicCoordinates;
import engine.math.numericalObjects.Vector3;
import engine.parser.utils.Logger;
import engine.utils.converters.StringConverter;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Instance {

	protected int id;
	private String name = null;
	private String stage = ScriptConstants.DEFAULT_STAGE;

	private BinaryTree<Attribute> attributes = null;
	private List<Effect> effects = null;
	private BinaryTree<Variable> variables = null;

	private List<Instance> subInstances = null;
	private Instance superInstance = null;
	private boolean slatedForRemoval = false;

	private Queue<Occupation> occupations = null;
	private int delayUntilNextTick = 0;

	private MoveableObject moveableObject;
	private MeshHub meshHub = null;

	public Instance(int id) {
		this.id = id;

		initMoveableObject();
        inheritAttributes(Collections.emptyList());
	}

    // ###################################################################################
    // ################################ Creation #########################################
    // ###################################################################################

	private void initMoveableObject() {
		moveableObject = new MoveableObject();
	}

	private void createAttributesIfNecessary() {
		if (attributes == null) {
			attributes = new BinaryTree<>();
		}
	}
	private void createEffectsIfNecessary() {
		if (effects == null) {
			effects = new LinkedList<>();
		}
	}
	private void createVariablesIfNecessary() {
		if (variables == null) {
			variables = new BinaryTree<>();
		}
	}
	private void createOccupationsIfNecessary() {
		if (occupations == null) {
			occupations = new LinkedList<>();
		}
	}
	private void createSubInstancesIfNecessary() {
		if (subInstances == null) {
			subInstances = new CopyOnWriteArrayList<>();
		}
	}

    public void inheritAttributes(final List<Instance> parents) {
		// sum up attribute values
	    getContainer().ifPresent(container -> {
	    	if (attributes != null) { attributes.clear(); }

			for (Integer id : Data.getAllAttributeIDs()) {
				ProtoAttribute protoAttribute = Data.getProtoAttribute(id);

				if (isAttributeInherited(protoAttribute, id)) {
					// weight parents and sum it up
					Map<Instance, Double> weights = new HashMap<>();
					double weightSumTmp = parents.stream()
							.map(parent -> {
								weights.put(parent, Math.random());
								return weights.get(parent);
							})
							.reduce(0d, Double::sum);
					final double weightSum = weightSumTmp == 0 ? 1 : weightSumTmp;

					// initial value
					double inheritedValue = 0;

					// inherent mutation
					Attribute containerAttribute = container.getAttribute(stage, id);
					if (containerAttribute != null && containerAttribute.getVariationProbability() > Math.random()) {
						inheritedValue += containerAttribute.getVariedValue();
					}

					// parent influences
					inheritedValue += parents.stream()
							.map(parent ->
									weights.get(parent)
											* ((double) parent.getPersonalAttributeValue(id))
											/ weightSum
							)
							.reduce(0d, (current, next) -> current + next);

					// set attribute
					if (inheritedValue != 0) {
						setAttribute(id, (int) inheritedValue);
					}
				}
			}
		});
    }

    public boolean isAttributeInherited(ProtoAttribute protoAttribute, int id) {
		if (protoAttribute == null) { return false; }
		if (! protoAttribute.isInherited()) { return false; }
		if (getPersonalAttributeValue(id) == 0) { return false; }

		return true;
	}

	// ###################################################################################
	// ################################ Running Scripts ##################################
	// ###################################################################################

	public Variable run(String textID, Variable[] parameters) {
		return getContainer().map(container -> {
			Script script = container.getScript(stage, textID);

			if (script == null && this == GameOptions.selectedInstance) {
				Logger.trace(getName() + " tried to run non-existing script '" + textID + "'");
			}
			return runScript(script, parameters);
		}).orElseGet(Variable::new);
	}

	public Variable run(Container scriptContainer, String textID, Variable[] parameters) {
		if (scriptContainer != null) {
			Script script = scriptContainer.getScript(stage, textID);

			if (script == null && this == GameOptions.selectedInstance) {
				Logger.trace(getName() + " tried to run non-existing script '" + textID + "' on " + scriptContainer.getName(null));
			}
			return runScript(script, parameters);
		}
		return new Variable();
	}

	public Variable run(Script script, Variable[] parameters) {

		if (script == null && this == GameOptions.selectedInstance) {
			Logger.trace(getName() + " tried to run non-existing script with parameters: " + Arrays.toString(parameters));
		}
		return runScript(script, parameters);
	}

	/**
	 * Do not use this method other than in a run() method!
	 * Finally runs the scripts and returns the result as a variable.
	 * If the script does not exist, 1 is returned.
	 * @param script to execute
	 * @param parameters of the script call
	 * @return result of script or 1
	 */
	private Variable runScript(Script script, Variable[] parameters) {
		if (script != null) {
			return script.run(this, parameters);
		} else {
			return new Variable(1); // script does not exist -> interpreted as true / condition fulfilled
		}
	}

	// ###################################################################################
	// ################################ Game Logic #######################################
	// ###################################################################################

	public void change(int containerId) {
		Container oldContainer = getContainer().orElse(null);
		id = containerId;
		Container newContainer = getContainer().orElse(null);

		inheritAttributes(Collections.emptyList());
		if (newContainer != null && newContainer.getType() == DataType.TILE) {
			((Tile) this).resetColors();
			((Tile) this).setChanged(true);
		}

		run(ScriptConstants.EVENT_CHANGE, new Variable[] {new Variable(oldContainer), new Variable(newContainer)});
	}

	/**
	 * Goes through all drives of the creature and sorts the triggered drives by their weight.
	 * @param drives to select needs from
	 */
	private WeightedList<Container> searchDrive(List<Container> drives) {

		WeightedList<Container> weightedDrives = new WeightedList<>();

		if (drives == null) { return weightedDrives; }

		for (Container drive : drives) {
			if (drive != null) {

				Variable condition = run(drive, ScriptConstants.EVENT_CONDITION, null);
				Variable weight = null;
				if (condition.notNull()) { // condition is fulfilled
					weight = run(drive, ScriptConstants.EVENT_GET_WEIGHT, null);
					if (weight.getDouble() > 0) {
						weightedDrives.add(drive, weight.getDouble());
					}
				}

				if (this == GameOptions.selectedInstance) {
					SelectedInstance.instance().putDrive(drive.getTextID(), condition, weight);
				}
			}
		}

		return weightedDrives;
	}

	/**
	 * The method tries to execute a process for one of the drives, starting with the most urgent one.
	 * @param weightedDrives weighted needs that need to be fulfilled
	 */
	private void actOnDrives(WeightedList<Container> weightedDrives) {

		for (Container drive : weightedDrives.list()) {

			List<Container> solutions = ((DriveContainer) drive).getSolutions(null);
			Container process = searchProcesses(solutions);

			if (process != null) {

				Variable result = run(process, ScriptConstants.EVENT_PROCESS, null);

				if (this == GameOptions.selectedInstance) {
					SelectedInstance.instance().putCurrentTask(process.getName(stage), result);
				}

				break; // only one process per tick
			}
			else if (Logger.hasLogLevel(Logger.LOG_TRACE) && this == GameOptions.selectedInstance) {
				Logger.trace("Instance " + getName() + " (" + stage + ") did not find process for " + drive.getName(null));

				Logger.trace("Acceptable solutions:");
				solutions.forEach(solution -> Logger.trace("   " + solution.getName(null) + " (known: " + knowsProcess(solution) + ")"));
			}
		}

	}

	/**
	 * Returns the first encountered process which fulfills its condition and returns it.
	 * Iteratively searches sub solutions.
	 * @param processes to look through
	 * @return process with fulfilled condition or null
	 */
	private Container searchProcesses(List<Container> processes) {
		if (processes == null) { return null; }

		for (Container process : processes) {
			if (process != null && knowsProcess(process)) {
				if (run(process, ScriptConstants.EVENT_CONDITION, null).notNull()) { // condition is fulfilled

					return process;

				} else { // condition not fulfilled -> if process, look through alternatives

					if (this == GameOptions.selectedInstance) {
						Logger.trace("Instance " + getName() + " could not fulfill condition of process " + process.getName(null));
					}

					if (process.getType() == DataType.PROCESS) {
						Container solution = searchProcesses(((ProcessContainer) process).getSolutions(stage));
						if (solution != null) {
							return solution;
						}
					}

				}
			}
		}

		return null;
	}

	/**
	 * Returns true if this instance knows the given process container.
	 * @param process of which we want to know whether we know it
	 * @return true if we know the process
	 */
	private boolean knowsProcess(Container process) {
		return getContainer()
				.filter(c -> c.getType() == DataType.CREATURE)
				.map(c -> {
					for (Container knowledge : ((CreatureContainer) c).getKnowledge(stage)) {
						if (knowledge.getTextID().equals(process.getTextID())) {
							return true;
						}
					}
					return false;
				})
				.orElse(false);
	}

	public boolean canGo(Tile from, Tile to) {
	    return ! run(ScriptConstants.EVENT_CAN_GO, new Variable[] {new Variable(from), new Variable(to)}).isNull();
	}

	public void addOccupation(int duration, Script callBackScript) {
		createOccupationsIfNecessary();
	    occupations.add(new Occupation(duration, callBackScript));
    }

	// ###################################################################################
	// ################################ Tick #############################################
	// ###################################################################################

	public void tick() {
		cleanVariables();
	    tickEffects();

	    Optional<Container> container = getContainer();
	    boolean runTickScripts = container.map(c -> c.isRunTickScripts(stage)).orElse(true);

	    if (runTickScripts && delayUntilNextTick <= 0) {
			if (this == GameOptions.selectedInstance) {
				SelectedInstance.instance().clear();
				Logger.trace("=== === Ticking " + getName());
			}

			if (occupations == null || occupations.isEmpty()) {
				// ----------- calculate drives
				container.filter(c -> c.getType() == DataType.CREATURE)
						.map(c -> ((CreatureContainer) c).getDrives(stage))
						.map(this::searchDrive)
						.ifPresent(this::actOnDrives);
			} else {
				// ----------- calculate occupations
				Occupation currentOccupation = occupations.peek();
				currentOccupation.tick();

				if (currentOccupation.isFinished()) {
					currentOccupation.callBack(this);
					occupations.poll();
				}
			}

			// ----------- calculate tick script
			run(ScriptConstants.EVENT_TICK, null);

		} else {
			if (runTickScripts) {
				delayUntilNextTick--;
			}
		}

        cleanEffects();
	}

	private void tickEffects() {
		if (effects == null) { return; }

	    for (Effect effect : effects) {
	        effect.tick(this);
        }
    }

    private void cleanEffects() {
		if (effects == null) { return; }

		List<Effect> newEffects = new LinkedList<>(); //new CopyOnWriteArrayList<>();

		for (Effect effect : effects) {
			if (effect.shouldBeRemoved(this)) {
				effect.callBack(this);
			} else {
				newEffects.add(effect);
			}
		}

		effects = newEffects;
    }

    private void cleanVariables() {
		if (variables == null) { return; }

		variables.forEach(variable -> {
			if (variable.isInvalid()) {
				variables.remove(variable.getId());
			}
		});
	}

	// ###################################################################################
	// ################################ Graphical ########################################
	// ###################################################################################

	public void render() {
		if (slatedForRemoval || GameOptions.reloadScripts) { return; }

		// render self
		MeshHub hub = getMeshHub();
		if (hub != null) { // we call getMeshHub() here, because it might have to be loaded from Data first
			hub.registerObject(moveableObject);
		}
		// render subs
		if (subInstances != null) {
			Optional<Container> container = Data.getContainer(id);
			container.filter(c -> c.getType() == DataType.TILE)
					.ifPresent(c -> {
						try {
							subInstances.forEach(Instance::render);
						} catch (ConcurrentModificationException e) { /* it's okay really */ }
					});
		}
	}

	/**
	 * Sets the current instance as a subinstance of the given instance.
	 * @param target where we want to put the current instance
	 */
	public void placeInto(Instance target) {
		if (target != null) {
			if (superInstance != null) {
				superInstance.removeSubInstance(this);
			}
			target.addSubInstance(this);
			superInstance = target;
			actualizeObjectPosition();

			run(ScriptConstants.EVENT_PLACE, new Variable[] {new Variable(target)});
		}
	}

	public void actualizeObjectPosition() {
		if (this == GameOptions.selectedInstance) {
			Logger.trace("Actualizing position of " + this);
		}

		if (superInstance != null && moveableObject != null) {
			Tile position = superInstance.getPosition();
			if (position != null) {
				// get rotation angles
				double pitch = GeographicCoordinates.getLatitude(position);
				double yaw = GeographicCoordinates.getLongitude(position);
				Vector3 pos;
				// set position
				/*if (position.getHeight() > position.getWaterHeight()) {
					pos = position.getTileMesh().getMid();
				} else {
					pos = position.getTileMesh().getWaterMid();
				}*/
//				double heightFactor = (Math.max(position.getHeight(), position.getWaterHeight()) + TopologyConstants.PLANET_MINIMUM_HEIGHT)
//						/ (TopologyConstants.PLANET_MINIMUM_HEIGHT + TopologyConstants.PLANET_MAXIMUM_HEIGHT);
//				pos = position.getTileMesh().getNormal().times(heightFactor);
				Vector3 mid = position.getTileMesh().getMid();
				int slot = position.getOpenSlot(this);
				if (slot == 0) {
					pos = mid;
				} else {
					Vector3 corner = position.getTileMesh().getScaledCorner(slot);
					pos = corner != null ? mid.plus(corner).times(0.5) : mid;
				}
				// set correct scale
				if (Data.getPlanet() != null) {
					double scaleFactor = 1d / (double) Data.getPlanet().getSize();
					moveableObject.setScale(scaleFactor, scaleFactor, scaleFactor);
				}
				// assign values to moveable object
				moveableObject.setPosition(pos);
				moveableObject.setRotation(pitch + Math.PI / 2d, -yaw + Math.PI / 2d, 0);
				moveableObject.setPreRotation(0, Math.random() * Math.PI * 2d, 0);
			}
		}
	}

	public void destroy() {
		if (id == Data.getMainInstance().getId()) {
			Logger.error("Cannot delete main instance!");
			return;
		}

		run(ScriptConstants.EVENT_DESTROY, new Variable[] {});

		recursiveSlatingForRemoval();
		if (variables != null) {
			variables.clear();
		}
		if (superInstance != null) {
			superInstance.removeSubInstance(this);
		}
	}

	protected void recursiveSlatingForRemoval() {
		if (this == GameOptions.selectedInstance) {
			GameOptions.selectedInstance = null;
		}

		slatedForRemoval = true;
		if (subInstances != null) {
			subInstances.forEach(Instance::recursiveSlatingForRemoval);
		}
	}

	// ###################################################################################
	// ################################ Sub Instances ####################################
	// ###################################################################################

	public boolean contains(int subId) {
		if (subInstances != null) {
			for (Instance instance : subInstances) {
				if (instance.getId() == subId) {
					return true;
				}
			}
		}
		return false;
	}

	private void addSubInstance(Instance instance) {
		if (instance != null) {
			createSubInstancesIfNecessary();
			subInstances.add(instance);
		}
	}
	private void removeSubInstance(Instance instance) {
		if (instance != null && subInstances != null) {
            subInstances.remove(instance);
		}
	}

    // ###################################################################################
    // ################################ Effects ##########################################
    // ###################################################################################

    public void addEffect(Effect effect) {
		createEffectsIfNecessary();
	    effects.add(effect);
    }

    public void deleteEffects(int containerID) {
		if (effects != null) {
			effects.removeIf(effect -> ((containerID == -1) || (effect.getId() == containerID)));
		}
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public Optional<Container> getContainer() {
		return Data.getContainer(id);
	}

	public Variable getProperty(PropertyKeys key) {
		return getProperty(key.key());
	}
	public Variable getProperty(String key) {
		return getContainer()
				.flatMap(c -> c.getProperty(stage, key))
				.orElseGet(Variable::new);
	}

	public void setMesh(String path) {
		meshHub = Data.getMeshHub(path);
		if (meshHub == null) {
			meshHub = Data.addMeshHub(path);
		}
	}

	public MeshHub getMeshHub() {
		if (meshHub == null) {
			meshHub = createMeshHub();
		}
		return meshHub;
	}

	private MeshHub createMeshHub() {
		return getContainer().map(container -> {
			String path = container.getMeshPath(stage);

			MeshHub meshHub = null;
			if (path != null) {
				meshHub = Data.addMeshHub(path);
				meshHub.setMeshOpacity(container.getOpacity(null));
			}
			return meshHub;
		}).orElse(null);
	}

	public void clearMeshHub() {
		if (meshHub != null) {
			meshHub.clear();
		}
		meshHub = null;
	}

	public boolean hasMesh() {
		return meshHub != null;
	}

    public String getName() {
        return name != null ? name : getContainer().map(c -> c.getName(stage)).orElse("Noname");
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Effect> getEffects() {
        return effects; //new LinkedList<>(effects); //new CopyOnWriteArrayList<>(effects);
    }

    public Instance getSubInstanceWithAttribute(int attributeID) {
		if (subInstances != null) {
			for (Instance instance : subInstances) {
				if (instance != null) {
					if (instance.getAttributeValue(attributeID) > 0) {
						return instance;
					}
				}
			}
		}
		return null;
	}

    public Instance getThisOrSubInstanceWithID(int containerID) {
		if (slatedForRemoval) { return null; }
	    if (id == containerID) { return this; }

	    if (subInstances != null) {
			for (Instance instance : subInstances) {
				if (instance != null && !instance.isSlatedForRemoval()) {
					if (instance.getId() == containerID) {
						return instance;
					}
				}
			}
		}
        return null;
    }

	public BinaryTree<Attribute> getAttributes() {
		return attributes;
	}

	public Attribute getAttribute(int attributeID) {
		createAttributesIfNecessary();

		Attribute attribute = attributes.get(attributeID);
		if (attribute == null) {
			attribute = new Attribute(attributeID, 0);
			attributes.insert(attribute);
		}
		return attribute;
	}

	public int getAttributeValue(int attributeID) {
		int value = 0;

		// personal data
		value += getPersonalAttributeValue(attributeID);

		// container data
		value += getContainerAttributeValue(attributeID);

		// effects
		value += getEffectsAttributeValue(attributeID);

		// bounds
		value = getBoundedAttributeValue(attributeID, value);

		return value;
	}

	public int getFullAttributeValue(int attributeID) {
		int value = 0;

		// values from self
		value += getAttributeValue(attributeID);

		// sub instances
		value += getSubInstancesAttributeValue(attributeID);

		return value;
	}

	public int getEffectsAttributeValue(int attributeID) {
		int value = 0;
		if (effects != null) {
			try {
				for (Effect effect : effects) {
					value += effect.getAttributeValue(attributeID);
				}
			} catch (ConcurrentModificationException e) {
				Logger.error("Concurrent modification during 'Instance.getAttributeValue()' (effect list is currently being modified)");
			}
		}
		return value;
	}

	public int getSubInstancesAttributeValue(int attributeID) {
		int value = 0;
		if (subInstances != null) {
			for (Instance sub : subInstances) {
				value += sub.getFullAttributeValue(attributeID);
			}
		}
		return value;
	}

	public int getPersonalAttributeValue(int attributeID) {
		if (attributes == null) { return 0; }

		Attribute attribute = attributes.get(attributeID);
		if (attribute == null) { return 0; }

		return attribute.getValue();
	}

	public int getContainerAttributeValue(int attributeID) {
		return getContainer().map(container -> container.getAttributeValue(stage, attributeID)).orElse(0);
	}

	private int getBoundedAttributeValue(int attributeID, int value) {
		if (this instanceof Effect) { return value; }

		// values that are zero and do not exist are returned as is (zero)
		if (value == 0 && attributes != null && attributes.get(attributeID) == null) { return value; }

		ProtoAttribute protoAttribute = Data.getProtoAttribute(attributeID);
		if (protoAttribute == null) { return value; }

		if (protoAttribute.hasLowerBound() && value < protoAttribute.getLowerBound()) {
			return protoAttribute.getLowerBound();
		} else if (protoAttribute.hasUpperBound() && value > protoAttribute.getUpperBound()) {
			return protoAttribute.getUpperBound();
		}

		return value;
	}

	/**
	 * Sets the personal attribute of the instance exactly to the given value.
	 * @param attributeID
	 * @param value
	 */
	public void setAttribute(int attributeID, int value) {
		if (attributeID >= 0) {
			createAttributesIfNecessary();
			Attribute attribute = attributes.get(attributeID);
			if (attribute == null) {
				attributes.insert(new Attribute(attributeID, value));
			} else {
				attribute.setValue(value);
			}
		}
	}
	public void addAttribute(int attributeID, int value) {
		setAttribute(attributeID, getPersonalAttributeValue(attributeID) + value);
	}

	public BinaryTree<Variable> getVariables() {
		return variables;
	}

	public Variable getVariable(String name) {
		if (variables == null) { return null; }

		Variable variable = variables.get(StringConverter.toID(name));

		if (variable != null && variable.isInvalid()) {
			variables.remove(StringConverter.toID(name));
			return null;
		}

		return variable;
	}
	public void addVariable(Variable variable) {
		createVariablesIfNecessary();
		variables.insert(variable);
	}
	public void removeVariable(String name) {
		if (variables != null) {
			variables.remove(StringConverter.toID(name));
		}
	}

	public int getId() {
		return id;
	}
	public void setId(int id) { this.id = id; }

	public Tile getPosition() {
		if (superInstance != null) {
			return superInstance.getPosition();
		} else {
			return null;
		}
	}

	public List<Instance> getSubInstances() {
		return subInstances;
	}

	public Instance getSuperInstance() {
		return superInstance;
	}
	public void setSuperInstance(Instance superInstance) {
		this.superInstance = superInstance;
	}

	public boolean isSlatedForRemoval() {
		return slatedForRemoval;
	}

	public Queue<Occupation> getOccupations() {
		return occupations;
	}

	public int getDelayUntilNextTick() {
		return delayUntilNextTick;
	}
	public void setDelayUntilNextTick(int delayUntilNextTick) {
		this.delayUntilNextTick = delayUntilNextTick;
	}

	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
		meshHub = null;
	}

	// ###################################################################################
	// ################################ Debugging ########################################
	// ###################################################################################

	public String getMemoryAddress() {
		return String.valueOf(System.identityHashCode(this));
	}

	public String toString() {
		return "Instance (id = " + getContainer().map(Container::getTextID).orElse("?") + (name != null ? " Name: " + name : "") + ")";
	}

}
