package engine.data.entities;

import constants.ScriptConstants;
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
import engine.data.structures.trees.binary.BinaryTree;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.graphics.objects.movement.MoveableObject;
import engine.logic.topology.GeographicCoordinates;
import engine.math.numericalObjects.Vector3;
import engine.parser.utils.Logger;
import engine.utils.converters.StringConverter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Instance {

	protected int id;

	private BinaryTree<Attribute> attributes;
	private BinaryTree<Variable> variables;
	private List<Instance> subInstances;
	private Instance superInstance = null;

	private boolean slatedForRemoval = false;

	private MoveableObject moveableObject = null;

	public Instance(int id) {
		this.id = id;

		moveableObject = new MoveableObject();
		attributes = new BinaryTree<>();
		variables = new BinaryTree<>();
		subInstances = new ArrayList<>(0);
	}

	// ###################################################################################
	// ################################ Game Logic #######################################
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

	private Variable runScript(Script script, Variable[] parameters) {
		if (script != null) {
			return script.run(this, parameters);
		} else {
			return new Variable(1); // script does not exist -> interpreted as true / condition fulfilled
		}
	}

	private void searchProcesses(List<ContainerIdentifier> processes) {
		if (processes != null && !processes.isEmpty()) {
			for (ContainerIdentifier process : processes) {
				Container container = process.retrieve();
				if (container != null && knowsProcess(container)) {
					if (!run(container, ScriptConstants.EVENT_CONDITION, null).isNull()) { // condition is fulfilled

						if (container.getType() == DataType.PROCESS) { // execute process
							run(container, ScriptConstants.EVENT_PROCESS, null);
						} else if (container.getType() == DataType.DRIVE) { // look through solutions of triggered drive
							searchProcesses(((DriveContainer) container).getSolutions());
						}
						break; // only one drive / one process for resolving is executed

					} else { // condition not fulfilled -> if process, look through alternatives

						if (container.getType() == DataType.PROCESS) {
							searchProcesses(((ProcessContainer) container).getSolutions());
						}

					}
				}
			}
		}
	}

	/**
	 * Returns true if the given entity knows the requested process.
	 * Returns true if the given container is a drive.
	 * @param container
	 * @return
	 */
	private boolean knowsProcess(Container container) {
		// drives require no knowledge of the processes
		if (container.getType() == DataType.DRIVE) {
			return true;
		}

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

	// ###################################################################################
	// ################################ Tick #############################################
	// ###################################################################################

	public void tick() {
		// ----------- calculate drives
		Container container = Data.getContainer(id);
		if (container != null && container.getType() == DataType.CREATURE) {
			searchProcesses(((CreatureContainer) container).getDrives());
		}

		// ----------- calculate tick script
		run(ScriptConstants.EVENT_TICK, null);
	}

	// ###################################################################################
	// ################################ Graphical ########################################
	// ###################################################################################

	public void render() {
		// render self
		Container container = Data.getContainer(id);
		if (container != null && container.getMeshHub() != null) {
			container.getMeshHub().registerObject(moveableObject);
		}
		// render subs
		for (Instance subInstance : subInstances) {
			subInstance.render();
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

	public void destroy() {
		slatedForRemoval = true;
		if (superInstance != null) {
			superInstance.removeSubInstance(this);
		}
		superInstance = null;
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
	// ################################ Getters and Setters ##############################
	// ###################################################################################

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

		// general data
		value += getSpeciesAttributeValue(attributeID);
		// personal data
		value += getPersonalAttributeValue(attributeID);

		return value;
	}

	public int getPersonalAttributeValue(int attributeID) {
		Attribute attribute = attributes.get(attributeID);
		return attribute != null? attribute.getValue() : 0;
	}

	public int getSpeciesAttributeValue(int attributeID) {
		Container container = Data.getContainer(id);
		return container != null? container.getAttributeValue(attributeID) : 0;
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
		return variables.get(StringConverter.toID(name));
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

	// ###################################################################################
	// ################################ Debugging ########################################
	// ###################################################################################

	public String toString() {
		return "Instance (id = " + Data.getContainer(id).getTextID() + ")";
	}

	public void printVariables() {
		IDInterface[] vars = variables.toArray();
		for (IDInterface idInterface : vars) {
			System.out.println((Variable) idInterface);
		}
	}
}
