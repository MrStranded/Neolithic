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
import engine.logic.GeographicCoordinates;
import engine.math.numericalObjects.Vector3;
import engine.utils.converters.StringConverter;

import java.util.ArrayList;
import java.util.List;

public class Instance {

	protected int id;

	private BinaryTree<Attribute> attributes;
	private BinaryTree<Variable> variables;
	private List<Instance> subInstances;
	private Instance superInstance = null;

	private boolean slatedForRemoval = false;

	private Tile position = null;
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
			return new Variable(1);
		}
	}

	private void searchProcesses(List<ContainerIdentifier> processes) {
		if (processes != null && !processes.isEmpty()) {
			for (ContainerIdentifier process : processes) {
				Container container = process.retrieve();
				if (container != null) {
					if (!run(container, ScriptConstants.EVENT_CONDITION, null).isNull()) { // condition is fulfilled
						System.out.println("!!!!!!!!!!!!!!!!! " + id + " has been triggered! " + process.toString());

						if (container.getType() == DataType.PROCESS) { // execute process
							run(container, ScriptConstants.EVENT_PROCESS, null);
						} else if (container.getType() == DataType.DRIVE) { // look through solutions of triggered drive
							searchProcesses(((DriveContainer) container).getSolutions());
						}

					} else { // condition not fulfilled -> if process, look through alternatives

						if (container.getType() == DataType.PROCESS) {
							searchProcesses(((ProcessContainer) container).getSolutions());
						}

					}
				}
			}
		}
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
		if (position != null) {
			Container container = Data.getContainer(id);
			if (container != null && container.getMeshHub() != null) {
				container.getMeshHub().registerObject(moveableObject);
			}
		}
		for (Instance subInstance : subInstances) {
			subInstance.render();
		}
	}

	public void setPosition(Tile tile) {
		if (position != null) {
			position.removeSubInstance(this);
		}
		tile.addSubInstance(this);
		position = tile;
		actualizeObjectPosition();
	}

	public void actualizeObjectPosition() {
		if (position != null && moveableObject != null) {
			double pitch = GeographicCoordinates.getLatitude(position);
			double yaw = GeographicCoordinates.getLongitude(position);
			Vector3 pos;
			if (position.getHeight() > position.getWaterHeight()) {
				pos = position.getTileMesh().getMid();
			} else {
				pos = position.getTileMesh().getWaterMid();
			}
			if (Data.getPlanet() != null) {
				double scaleFactor = 1d / (double) Data.getPlanet().getSize();
				moveableObject.setScale(scaleFactor, scaleFactor, scaleFactor);
			}
			moveableObject.setPosition(pos);
			moveableObject.setRotation(pitch + Math.PI/2d, -yaw + Math.PI/2d, 0);
			moveableObject.setPreRotation(0,Math.random() * Math.PI*2d,0);
		}
	}

	public void destroy() {
		slatedForRemoval = true;
		if (superInstance != null) {
			superInstance.removeSubInstance(this);
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
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public int getAttribute(int attributeID) {
		int value = 0;

		// general data
		Container container = Data.getContainer(id);
		value += container != null? container.getAttribute(attributeID) : 0;

		// personal data
		Attribute attribute = attributes.get(attributeID);
		value += attribute != null? attribute.getValue() : 0;

		return value;
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

	public Tile getPosition() { return position; }

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
