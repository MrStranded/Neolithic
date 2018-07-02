package main.data.personal;

import main.data.ID;

/**
 * A 'pointer' to the ProtoAttributes in the Data class.
 *
 * Created by Michael on 25.09.2017.
 */
public class Attribute implements ID {

	private int id;
	private int value;

	public Attribute(int id,int value) {
		this.id = id;
		this.value = value;
	}

	public Attribute(Attribute other) {
		id = other.id;
		value = other.value;
	}

	/**
	 * Adds the value of another Attribute to the value of this Attribute and returns it.
	 * ATTENTION: The addition is inplace.
	 *
	 * @param other Attribute
	 * @return this (modified)
	 */
	public Attribute add (Attribute other) {
		if (id == other.id) {
			value += other.value;
		}
		return this;
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}

}
