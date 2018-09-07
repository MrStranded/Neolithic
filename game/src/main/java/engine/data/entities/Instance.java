package engine.data.entities;

import engine.data.IDInterface;
import engine.data.attributes.Attribute;
import engine.data.planetary.Tile;
import engine.data.proto.Container;
import engine.data.Data;
import engine.data.structures.Script;
import engine.data.structures.trees.binary.BinaryTree;
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

	public void runScript(String textID, Variable[] parameters) {
		Container container = Data.getContainer(id);
		if (container != null) {
			Script script = container.getScript(textID);
			if (script != null) {
				script.run(this, parameters);
			}
		}
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

	private void actualizeObjectPosition() {
		if (position != null && moveableObject != null) {
			double pitch = GeographicCoordinates.getLatitude(position);
			double yaw = GeographicCoordinates.getLongitude(position);
			Vector3 pos;
			if (position.getHeight() > position.getWaterHeight()) {
				pos = position.getTileMesh().getMid();
			} else {
				pos = position.getTileMesh().getWaterMid();
			}
			moveableObject.setScale(0.05d,0.05d,0.05d);
			moveableObject.setPosition(pos);
			moveableObject.setRotation(pitch + Math.PI/2d, -yaw + Math.PI/2d, 0);
			moveableObject.setPreRotation(0,Math.random() * Math.PI*2d,0);
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
		subInstances.add(instance);
	}
	public void removeSubInstance(Instance instance) {
		subInstances.remove(instance);
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
