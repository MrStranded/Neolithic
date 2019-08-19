package engine.parser.tokenization;

import engine.parser.constants.TokenConstants;
import engine.parser.constants.TokenType;

public class Token {

	private String value;
	private TokenType type;
	private int line; // line in which token occured

	public Token(TokenType type, String value, int line) {
		this.type = type;
		this.value = value;
		this.line = line;
	}

	public int getPrecedence() {
		TokenConstants tokenConstant = TokenConstants.getCorrespondingConstant(this);
		if (tokenConstant == null) {
			return 0;
		}
		return tokenConstant.getPrecedence();
	}

	public Token flipValue() {
		value = "-" + value;
		return this;
	}

	public String getValue() {
		return value;
	}

	public TokenType getType() {
		return type;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getLine() {
		return line;
	}

	public String toString() {
		return type.toString() + ": " + value;
	}
}
