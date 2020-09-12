package engine.data.entities;

import constants.ScriptConstants;
import engine.data.behaviour.Occupation;
import engine.data.identifiers.ContainerIdentifier;
import engine.data.IDInterface;
import engine.data.attributes.Attribute;
import engine.data.planetary.Tile;
import engine.data.proto.*;
import engine.data.Data;
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
import java.util.concurrent.CopyOnWriteArraySet;

public class Instance {

	protected int id;
	private String name = null;

	private BinaryTree<Attribute> attributes = null;
	private List<Effect> effects = null;
	private BinaryTree<Variable> variables = null;

	private List<Instance> subInstances = null;
	private Instance superInstance = null;

	private Queue<Occupation> occupations = null;

	private boolean slatedForRemoval = false;
	private int delayUntilNextTick = 0;

	private MoveableObject moveableObject;
	private MeshHub meshHub = null;

	public Instance(int id) {
		this.id = id;

		initMoveableObject();
        inheritAttributes();
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
			subInstances = new ArrayList<>();
		}
	}

    public void inheritAttributes() {
	    Container container = Data.getContainer(id);

	    if (container != null) {
	    	BinaryTree<Attribute> tree = container.getAttributes();
	    	if (tree != null) {
	    		tree.forEach(attributeID -> {
					addAttribute(attributeID.getId(), container.getAttributeValue(attributeID.getId()));
				});
			}
        }
    }

	// ###################################################################################
	// ################################ Running Scripts ##################################
	// ###################################################################################

	public Variable run(String textID, Variable[] parameters) {
		Container container = Data.getContainer(id);
		if (container != null) {
			Script script = container.getScript(textID);
			return runScript(script, parameters);
		}
		return new Variable();
	}

	public Variable run(Container scriptContainer, String textID, Variable[] parameters) {
		if (scriptContainer != null) {
			Script script = scriptContainer.getScript(textID);
			return runScript(script, parameters);
		}
		return new Variable();
	}

	public Variable run(Script script, Variable[] parameters) {
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
		id = containerId;
		inheritAttributes();
		if (Data.getContainer(containerId).getType() == DataType.TILE) {
			((Tile) this).resetColors();
			((Tile) this).setChanged(true);
		}
	}

	/**
	 * Goes through all drives of the creature and sorts the triggered drives by their weight.
	 * Then the method tries to execute a process of one of the drives, starting with the most important one.
	 * @param drives to look through
	 */
	private void searchDrive(List<ContainerIdentifier> drives) {
		WeightedList<Container> weightedDrives = new WeightedList<>();

		if (drives != null) {
			for (ContainerIdentifier drive : drives) {
				Container container = drive.retrieve();
				if (container != null) {

					if (!run(container, ScriptConstants.EVENT_CONDITION, null).isNull()) { // condition is fulfilled

						double weight = run(container, ScriptConstants.EVENT_GET_WEIGHT, null).getDouble();
						weightedDrives.add(container, weight);

					}
				}
			}
		}

		for (Container drive : weightedDrives.list()) {
			//if (drive.getType() == DataType.DRIVE) { // look through solutions of triggered drive

				Container process = searchProcesses(((DriveContainer) drive).getSolutions());
				if (process != null) {

					run(process, ScriptConstants.EVENT_PROCESS, null);
					break; // only one process per tick

				}
			//}
		}

	}

	/**
	 * Returns the first encountered process which fulfills its condition and returns it.
	 * Iteratively searches sub solutions.
	 * @param processes to look through
	 * @return process with fulfilled condition or null
	 */
	private Container searchProcesses(List<ContainerIdentifier> processes) {
		if (processes != null) {
			for (ContainerIdentifier process : processes) {
				Container container = process.retrieve();
				if (container != null && knowsProcess(container)) {
					if (!run(container, ScriptConstants.EVENT_CONDITION, null).isNull()) { // condition is fulfilled

						return container;

					} else { // condition not fulfilled -> if process, look through alternatives

						if (container.getType() == DataType.PROCESS) {
							Container solution = searchProcesses(((ProcessContainer) container).getSolutions());
							if (solution != null) {
								return solution;
							}
						}

					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns true if the given entity knows the requested process.
	 * @param container
	 * @return
	 */
	private boolean knowsProcess(Container container) {
		// check the knowledge base of the creature to see whether it knows the process
		Container selfContainer = Data.getContainer(id);
		if (selfContainer.getType() == DataType.CREATURE) {
			for (ContainerIdentifier knowledge : ((CreatureContainer) selfContainer).getKnowledge()) {
				if (knowledge.identifies(container)) {
					return true;
				}
			}
		}
        //Logger.log(Data.getContainer(id).getType() + " does not know " + container.getTextID());
		return false;
	}

	public boolean canGo(Tile from, Tile to) {
	    return !run(ScriptConstants.EVENT_CAN_GO, new Variable[] {new Variable(from), new Variable(to)}).isNull();
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
	    boolean runTickScripts = container.map(Container::isRunTickScripts).orElse(true);

	    if (runTickScripts && delayUntilNextTick <= 0) {
	    	delayUntilNextTick = 0;

			if (occupations == null || occupations.isEmpty()) {
				// ----------- calculate drives
				if (container.isPresent() && container.get().getType() == DataType.CREATURE) {
					searchDrive(((CreatureContainer) container.get()).getDrives());
				}
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
			if (runTickScripts) { delayUntilNextTick--; }
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

		for (IDInterface variable : variables.toArray()) {
			if (((Variable) variable).isInvalid()) {
				variables.remove(variable.getId());
			}
		}
	}

	// ###################################################################################
	// ################################ Graphical ########################################
	// ###################################################################################

	public void render() {
		if (slatedForRemoval) {
			return;
		}
		// render self
		if (getMeshHub() != null) { // we call getMeshHub() here, because it might have to be loaded from Data first
			meshHub.registerObject(moveableObject);
		}
		// render subs
		if (subInstances != null) {
			if (Data.getContainer(id) != null && Data.getContainer(id).getType() == DataType.TILE) { // only tiles draw their subs?
				CopyOnWriteArraySet<Instance> subs = new CopyOnWriteArraySet<>(subInstances);
				for (Instance subInstance : subs) {
					if (subInstance != null) {
						subInstance.render();
					}
				}
			}
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
		}
	}

	public void actualizeObjectPosition() {
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
				pos = position.getTileMesh().getMid();
				// set correct scale
				if (Data.getPlanet() != null) {
					double scaleFactor = 1d / (double) Data.getPlanet().getSize();
					moveableObject.setScale(scaleFactor, scaleFactor, scaleFactor);
				}
				// assign values to moveable object
				moveableObject.setPosition(pos);
				moveableObject.setRotation(pitch + Math.PI / 2d, -yaw + Math.PI / 2d, 0);
				moveableObject.setPreRotation(0, Math.random() * Math.PI * 2d * 0d, 0);
			}
		}
	}

	public void destroy() {
		if (id == Data.getMainInstance().getId()) {
			Logger.error("Cannot delete main instance!");
			return;
		}

		recursiveSlatingForRemoval();
		if (variables != null) {
			variables.clear();
		}
		if (superInstance != null) {
			superInstance.removeSubInstance(this);
		}
	}

	private void recursiveSlatingForRemoval() {
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
		return Optional.ofNullable(Data.getContainer(id));
	}

	public void setMesh(String path) {
		meshHub = Data.getMeshHub(path);
		if (meshHub == null) {
			meshHub = Data.addMeshHub(path);
		}
	}

	public MeshHub getMeshHub() {
		if (meshHub == null) {
			Container container = Data.getContainer(id);
			if (container != null) {
				meshHub = container.getMeshHub();
			}
		}
		return meshHub;
	}

    public String getName() {
        return name != null ? name : Data.getContainer(id) != null ? Data.getContainer(id).getName() : "Noname";
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
		// effects
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

	public int getFullAttributeValue(int attributeID) {
		int value = 0;

		// values from self
		value += getPersonalAttributeValue(attributeID);
		// sub instances
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
		if (attributeID >= 0) {
			createAttributesIfNecessary();
			attributes.insert(new Attribute(attributeID, value));
		}
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
	// no setId, use change(id) instead!

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
	private void setSlatedForRemoval(boolean slatedForRemoval) {
		this.slatedForRemoval = slatedForRemoval;
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

	// ###################################################################################
	// ################################ Debugging ########################################
	// ###################################################################################

	public String toString() {
		return "Instance (id = " + Data.getContainer(id).getTextID() + (name != null ? " Name: " + name : "") + ")";
	}

	public void printVariables() {
		IDInterface[] vars = variables.toArray();
		for (IDInterface idInterface : vars) {
			System.out.println((Variable) idInterface);
		}
	}

	public void printAttributes() {
        IDInterface[] atts = attributes.toArray();
        for (IDInterface idInterface : atts) {
            System.out.println((Attribute) idInterface);
        }
    }
}
