package engine.data.attributes;

import engine.data.Data;
import engine.data.IDInterface;
import engine.data.proto.Container;
import engine.data.proto.ProtoAttribute;
import engine.data.variables.DataType;

import java.util.Optional;

public class Attribute implements IDInterface {

	private int id;
	private int value = 0;
	private boolean bounded = false;

	public Attribute(int id) {
		this.id = id;
	}

	public Attribute(int id, int value) {
		this.id = id;
		this.value = value;

		establishBoundedness();
	}

	// ###################################################################################
	// ################################ Bounds ###########################################
	// ###################################################################################

	private void establishBoundedness() {
		ProtoAttribute protoAttribute = Data.getProtoAttribute(id);
		if (protoAttribute != null) {
			bounded = protoAttribute.hasLowerBound() || protoAttribute.hasUpperBound();
		}
		if (bounded) { checkBounds(); }
	}

	private void checkBounds() {
		ProtoAttribute protoAttribute = Data.getProtoAttribute(id);
		if (protoAttribute == null) { return; }

		if (protoAttribute.hasLowerBound() && value < protoAttribute.getLowerBound()) {
			value = protoAttribute.getLowerBound();
		} else if (protoAttribute.hasUpperBound() && value > protoAttribute.getUpperBound()) {
			value = protoAttribute.getUpperBound();
		}
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
		value = ((Attribute) other).value;
		return this;
	}

	public int getVariedValue() { return 0; }
	public double getVariationProbability() { return 0; }

	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;

		if (bounded) { checkBounds(); }
	}

	public boolean isBounded() {
		return bounded;
	}

	public void setBounded(boolean bounded) {
		this.bounded = bounded;

		if (bounded) { checkBounds(); }
	}

	@Override
	public String toString() {
		return "Attribute (id = " + Data.getProtoAttribute(id).getTextID() + ": " + value + ")";
	}
}
