package enums.script;

/**
 * Created by Michael on 11.09.2017.
 */
public enum Command {

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

}
