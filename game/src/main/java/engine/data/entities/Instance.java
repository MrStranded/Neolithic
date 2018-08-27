package engine.data.entities;

import engine.data.attributes.Attribute;
import engine.data.proto.Container;
import engine.data.proto.Data;
import engine.data.structures.trees.binary.BinaryTree;

import java.util.ArrayList;
import java.util.List;

public class Instance {

	protected int id;

	private BinaryTree<Attribute> attributes;
	private List<Instance> subInstances;

	public Instance(int id) {
		attributes = new BinaryTree<>();
		subInstances = new ArrayList<>(4);
	}

	// ###################################################################################
	// ################################ Graphical ########################################
	// ###################################################################################

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

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
