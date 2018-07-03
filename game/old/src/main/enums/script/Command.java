package main.enums.script;

import main.enums.TextEnumInterface;

/**
 * Created by Michael on 11.09.2017.
 */
public enum Command implements TextEnumInterface {

	NONE ("none"),
	
	MOVE ("move"),
	IF ("if"),
	RANDOM ("random"),
	GETCLASS ("getClass"),
	FILLWORLD ("fillWorld"),
	WORLDSIZE ("worldSize"),
	GETTILE ("getTile"),
	SETTILE ("setTile"),
	SETHEIGHT ("setHeight"),
	CALL ("call");

	// ###################################################################################
	// ################################ Modification #####################################
	// ###################################################################################

	private final String text;

	Command() {
		text = this.name();
	}
	Command(String s) {
		text = s;
	}

	public boolean equals(String other) {
		return text.equals(other);
	}

	public String toString() {
		return this.text;
	}

	public Command get(String name) {
		Command[] types = Command.values();
		for (Command t : types) {
			if (t.toString().equals(name)) {
				return t;
			}
		}
		return NONE;
	}
	
}
