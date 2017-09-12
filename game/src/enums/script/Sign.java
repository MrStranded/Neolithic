package enums.script;

/**
 * Created by Michael on 11.09.2017.
 */
public enum Sign {

	COMMA (','),
	END_OF_BLOCK (';'),
	QUOTATION_MARK ('"'),
	COMMENT ('#'),
	DOT ('.'),

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
}
