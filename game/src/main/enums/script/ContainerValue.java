package main.enums.script;

import main.enums.TextEnumInterface;

/**
 * Created by Michael on 26.09.2017.
 */
public enum ContainerValue implements TextEnumInterface {

	NONE ("none"),
	
	// general
	NAME ("name"),

	// Attributes
	FLAG ("flag"),
	MUTATION ("mutate"),

	// DNA
	DNA ("addToDNA"),

	// WorldGen
	DEFAULTTILE ("defaultTile"),
	DEFAULTTILEHEIGHT ("defaultTileHeight"),

	DEFAULTFLUID ("defaultFluid"),
	DEFAULTFLUIDHEIGHT ("defaultFluidHeight"),

	MOUNTAINPERCENT ("mountainPercent"),
	VALLEYPERCENT ("valleyPercent")
	;

	// ###################################################################################
	// ################################ Modification #####################################
	// ###################################################################################

	private final String text;

	ContainerValue() {
		text = this.name();
	}
	ContainerValue(String s) {
		text = s;
	}

	public boolean equals(String other) {
		return text.equals(other);
	}

	public String toString() {
		return this.text;
	}

	public ContainerValue get(String name) {
		ContainerValue[] types = ContainerValue.values();
		for (ContainerValue t : types) {
			if (t.toString().equals(name)) {
				return t;
			}
		}
		return NONE;
	}
	
}
