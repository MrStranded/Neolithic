package engine.data.entities;

import engine.data.IDInterface;
import engine.data.attributes.Attribute;
import engine.data.planetary.Tile;
import engine.data.proto.Container;
import engine.data.proto.Data;
import engine.data.structures.Script;
import engine.data.structures.trees.binary.BinaryTree;
import engine.data.variables.Variable;
import engine.graphics.objects.GraphicalObject;
import engine.graphics.renderer.Renderer;
import engine.graphics.renderer.color.RGBA;
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
	private GraphicalObject graphicalObject = null;

	public Instance(int id) {
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
		if (graphicalObject != null && position != null) {
			Renderer.self.addToRenderList(graphicalObject);
		}
		for (Instance subInstance : subInstances) {
			subInstance.render();
		}
	}

	public void setGraphicalObject(GraphicalObject graphicalObject) {
		this.graphicalObject = graphicalObject;
		graphicalObject.setScale(0.1d,0.1d,0.1d);
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
		if (position != null && graphicalObject != null) {
			double pitch = GeographicCoordinates.getLatitude(position);
			double yaw = GeographicCoordinates.getLongitude(position);
			Vector3 pos = position.getTileMesh().getMid();
			graphicalObject.setPosition(pos);
			graphicalObject.setRotation(pitch, yaw, 0);
			graphicalObject.getMesh().setColor(new RGBA(0,1,1));
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

	// ###################################################################################
	// ################################ Debugging ########################################
	// ###################################################################################

	public void printVariables() {
		IDInterface[] vars = variables.toArray();
		for (IDInterface idInterface : vars) {
			System.out.println((Variable) idInterface);
		}
	}
}
