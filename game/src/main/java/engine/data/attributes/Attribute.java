package engine.data.attributes;

import engine.data.Data;
import engine.data.IDInterface;

public class Attribute implements IDInterface {

	private int id;
	private int value = 0;
	private int variation = 0;

	public Attribute(int id) {
		this.id = id;
	}

	public Attribute(int id, int value) {
		this.id = id;
		this.value = value;
	}

	public Attribute(int id, int value, int variation) {
		this.id = id;
		this.value = value;
		this.variation = variation;
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
		if (variation == 0) {
			return value;
		}
		return value - variation + (int) ((double) (variation + 1) * (Math.random() * 2d));
	}
	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Attribute (id = " + Data.getProtoAttribute(id).getTextID() + ": " + value + ")";
	}
}
