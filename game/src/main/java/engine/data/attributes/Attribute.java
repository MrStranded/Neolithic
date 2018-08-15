package engine.data.attributes;

import engine.data.IDInterface;

public class Attribute implements IDInterface {

	private int id;
	private int value = 0;

	public Attribute(int id) {
		this.id = id;
	}

	public Attribute(int id, int value) {
		this.id = id;
		this.value = value;
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	@Override
	public int getId() {
		return id;
	}

	@Override
	public IDInterface merge(IDInterface other) {
		value += ((Attribute) other).value;
		return this;
	}

	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
}
