package enums.script;

/**
 * Created by Michael on 26.09.2017.
 */
public enum ContainerValue {

	// general
	NAME ("name"),

	// Attributes
	FLAG ("flag"),
	MUTATION ("mutate"),

	// DNA
	DNA ("addToDNA"),
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
}
