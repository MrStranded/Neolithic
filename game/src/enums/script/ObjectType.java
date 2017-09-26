package enums.script;

/**
 * Created by Michael on 26.09.2017.
 */
public enum ObjectType {

	NONE ("none"),
	ATTRIBUTE ("Attribute"),
	TILE ("Tile"),
	FLUID ("Fluid"),
	OBJECT ("Object"),
	CREATURE ("Creature");

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

}
