package main.enums.script;

import main.enums.TextEnumInterface;

/**
 * Created by Michael on 26.09.2017.
 */
public enum ObjectType implements TextEnumInterface {

	NONE ("none"),
	ATTRIBUTE ("Attribute"),
	TILE ("Tile"),
	FLUID ("Fluid"),
	OBJECT ("Object"),
	CREATURE ("Creature"),
	WORLDGEN ("WorldGen"),
	FORMATION ("Formation");

	// ###################################################################################
	// ################################ Modification #####################################
	// ###################################################################################

	private final String text;

	ObjectType() {
		text = this.name();
	}
	ObjectType(String s) {
		text = s;
	}

	public boolean equals(String other) {
		return text.equals(other);
	}

	public String toString() {
		return this.text;
	}

	public ObjectType get(String name) {
		ObjectType[] types = ObjectType.values();
		for (ObjectType t : types) {
			if (t.toString().equals(name)) {
				return t;
			}
		}
		return NONE;
	}

}
