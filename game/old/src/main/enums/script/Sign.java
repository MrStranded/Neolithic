package main.enums.script;

import main.enums.TextEnumInterface;

/**
 * Created by Michael on 11.09.2017.
 *
 * Holds all the system-relevant chars used in .def files.
 */
public enum Sign implements TextEnumInterface {

	NONE ('n'),
	
	COMMA (','),
	SEMICOLON (';'),
	QUOTATION_MARK ('"'),
	COMMENT ('#'),
	DOT ('.'),
	COLON (':'),

	OPEN_EXPRESSION ('('),
	CLOSE_EXPRESSION (')'),

	OPEN_ARRAY ('['),
	CLOSE_ARRAY (']'),

	OPEN_BLOCK ('{'),
	CLOSE_BLOCK ('}');

	// ###################################################################################
	// ################################ Modification #####################################
	// ###################################################################################

	private final char c;

	Sign() {
		c = '~';
	}
	Sign(char c) {
		this.c = c;
	}

	public char getChar() {
		return c;
	}

	public boolean equals(String other) {
		if ((other != null) && (other.length() == 1)) {
			return other.charAt(0) == c;
		}
		return false;
	}

	public String toString() {
		return String.valueOf(c);
	}

	public Sign get(String name) {
		Sign[] types = Sign.values();
		for (Sign t : types) {
			if (t.toString().equals(name)) {
				return t;
			}
		}
		return NONE;
	}
}
