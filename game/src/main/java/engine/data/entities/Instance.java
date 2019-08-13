package engine.data.entities;

import constants.ScriptConstants;
import engine.data.behaviour.Occupation;
import engine.data.identifiers.ContainerIdentifier;
import engine.data.IDInterface;
import engine.data.attributes.Attribute;
import engine.data.planetary.Tile;
import engine.data.proto.Container;
import engine.data.Data;
import engine.data.proto.CreatureContainer;
import engine.data.proto.DriveContainer;
import engine.data.proto.ProcessContainer;
import engine.data.Script;
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
import java.util.concurrent.CopyOnWriteArraySet;

public class Instance {

	protected int id;
	private String name = null;

	private BinaryTree<Attribute> attributes;
	private List<Effect> effects;
	private BinaryTree<Variable> variables;

	private List<Instance> subInstances;
	private Instance superInstance = null;

	private Queue<Occupation> occupations;

	private boolean slatedForRemoval = false;

	private MoveableObject moveableObject = null;
	private MeshHub meshHub = null;

	public Instance(int id) {
		this.id = id;

		attributes = new BinaryTree<>();
		effects = new CopyOnWriteArrayList<>();
		variables = new BinaryTree<>();

		subInstances = new ArrayList<>(0);
		occupations = new LinkedList<>();

        moveableObject = new MoveableObject();

        inheritAttributes();
	}

    // ###################################################################################
    // ################################ Creation #########################################
    // ###################################################################################

    private void inheritAttributes() {
	    Container container = Data.getContainer(id);

	    if (container != null) {
	        for (IDInterface attributeID : container.getAttributes()) {
	            addAttribute(attributeID.getId(), container.getAttributeValue(attributeID.getId()));
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
        Logger.log(Data.getContainer(id).getType() + " does not know " + container.getTextID());
		return false;
	}

	public boolean canGo(Tile from, Tile to) {
	    return !run(ScriptConstants.EVENT_CAN_GO, new Variable[] {new Variable(from), new Variable(to)}).isNull();
	}

	public void addOccupation(int duration, Script callBackScript) {
	    occupations.add(new Occupation(duration, callBackScript));
    }

	// ###################################################################################
	// ################################ Tick #############################################
	// ###################################################################################

	public void tick() {
		cleanVariables();
	    tickEffects();

	    if (occupations.isEmpty()) {
            // ----------- calculate drives
            Container container = Data.getContainer(id);
            if (container != null && container.getType() == DataType.CREATURE) {
                searchDrive(((CreatureContainer) container).getDrives());
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

        cleanEffects();
	}

	private void tickEffects() {
	    for (Effect effect : effects) {
	        effect.tick(this);
        }
    }

    private void cleanEffects() {
		List<Effect> newEffects = new CopyOnWriteArrayList<>();

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
		// render self
		if (getMeshHub() != null) { // we call getMeshHub() here, because it might have to be loaded from Data first
			meshHub.registerObject(moveableObject);
		}
		// render subs
        CopyOnWriteArraySet<Instance> subs = new CopyOnWriteArraySet<>(subInstances);
        for (Instance subInstance : subs) {
            if (subInstance != null) {
                subInstance.render();
            }
        }
	}

	/**
	 * Sets the current instance as a subinstance of the given instance.
	 * @param instance where we want to put the current instance
	 */
	public void placeInto(Instance instance) {
		if (instance != null) {
			if (superInstance != null) {
				superInstance.removeSubInstance(this);
			}
			instance.addSubInstance(this);
			superInstance = instance;
			actualizeObjectPosition();
		}
	}

	/**
	 * Sets the current instance at the tile position of the given instance.
	 * @param instance at whose tile position we want to put the current instance
	 */
	public void placeAt(Instance instance) {
		if (instance != null) {
			if (superInstance != null) {
				superInstance.removeSubInstance(this);
			}
			Tile position = instance.getPosition();
			if (position != null) {
				position.addSubInstance(this);
				superInstance = position;
			}
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
				if (position.getHeight() > position.getWaterHeight()) {
					pos = position.getTileMesh().getMid();
				} else {
					pos = position.getTileMesh().getWaterMid();
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

	/** remove?
	 *
	 * @param other
	 */
	public void replaceBy(Instance other) {
	    if (superInstance != null) { superInstance.addSubInstance(other); }

	    for (Instance subInstance : subInstances) {
	        other.addSubInstance(subInstance);
        }

        destroy();
    }

	public void destroy() {
		if (id == Data.getMainInstance().getId()) {
			Logger.error("Cannot delete main instance!");
		}

		recursiveSlatingForRemoval();
		cleanVariables();
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
		for (Instance instance : subInstances) {
			if (instance.getId() == subId) {
				return true;
			}
		}
		return false;
	}

	public void addSubInstance(Instance instance) {
		if (instance != null) {
			instance.setSuperInstance(this);
			subInstances.add(instance);
		}
	}
	public void removeSubInstance(Instance instance) {
		if (instance != null) {
			instance.setSuperInstance(null);
            subInstances.remove(instance);
		}
	}

    // ###################################################################################
    // ################################ Effects ##########################################
    // ###################################################################################

    public void addEffect(Effect effect) {
	    effects.add(effect);
    }

    public void deleteEffects(int containerID) {
		effects.removeIf(effect -> ((containerID == -1) || (effect.getId() == containerID)));
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

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

    public List<Instance> getEffects() {
        return new CopyOnWriteArrayList<>(effects);
    }

    public Instance getSubInstanceWithAttribute(int attributeID) {
		for (Instance instance : subInstances) {
			if (instance != null) {
				if (instance.getAttributeValue(attributeID) > 0) {
					return instance;
				}
			}
		}
		return null;
	}

    public Instance getThisOrSubInstanceWithID(int containerID) {
	    if (id == containerID) { return this; }

        for (Instance instance : subInstances) {
            if (instance != null) {
                if (instance.getId() == containerID) {
                    return instance;
                }
            }
        }
        return null;
    }

	public Attribute getAttribute(int attributeID) {
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
		try {
			for (Instance effect : new CopyOnWriteArrayList<>(effects)) {
				value += effect.getAttributeValue(attributeID);
			}
		} catch (Exception e) {
			Logger.error("Concurrent modification during 'Instance.getAttributeValue()' (effect list is currently being modified)");
		}

		return value;
	}

	public int getPersonalAttributeValue(int attributeID) {
		Attribute attribute = attributes.get(attributeID);
		return attribute != null? attribute.getValue() : 0;
	}

	/**
	 * Sets the personal attribute of the instance exactly to the given value.
	 * @param attributeID
	 * @param value
	 */
	public void setAttribute(int attributeID, int value) {
		if (attributeID >= 0) {
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
			attributes.insert(new Attribute(attributeID, value));
		}
	}

	public Variable getVariable(String name) {
		Variable variable = variables.get(StringConverter.toID(name));

		if (variable != null && variable.isInvalid()) {
			variables.remove(StringConverter.toID(name));
			return null;
		}

		return variable;
	}
	public void addVariable(Variable variable) {
		variables.insert(variable);
	}
	public void removeVariable(String name) {
		variables.remove(StringConverter.toID(name));
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

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
	public void setSlatedForRemoval(boolean slatedForRemoval) {
		this.slatedForRemoval = slatedForRemoval;
	}

	public Queue<Occupation> getOccupations() {
		return occupations;
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
