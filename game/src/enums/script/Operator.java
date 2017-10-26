package enums.script;

import com.sun.org.apache.xpath.internal.operations.NotEquals;
import enums.TextEnumInterface;

import java.time.temporal.Temporal;

/**
 * logical and mathematical operators
 *
 * Created by Michael on 11.09.2017.
 */
public enum Operator implements TextEnumInterface {

	NONE ("none"),
	
	ADDITION ("+"),
	SUBTRACTION ("-"),
	MULTIPLICATION ("*"),
	DIVISION ("/"),
	MODULO ("%"),

	LET	("="),
	PARAMETERS (":"),

	EQUAL ("=="),
	NOT_EQUAL ("!="),
	GREATER (">"),
	GREATER_OR_SAME (">="),
	SMALLER ("<"),
	SMALLER_OR_SAME ("<="),

	AND ("&&"),
	OR ("||");

	// ###################################################################################
	// ################################ Modification #####################################
	// ###################################################################################

	private final String text;

	Operator() {
		text = this.name();
	}
	Operator(String s) {
		text = s;
	}

	public boolean equals(String other) {
		return text.equals(other);
	}

	public String toString() {
		return this.text;
	}

	public Operator get(String name) {
		Operator[] types = Operator.values();
		for (Operator t : types) {
			if (t.toString().equals(name)) {
				return t;
			}
		}
		return NONE;
	}
	
}
