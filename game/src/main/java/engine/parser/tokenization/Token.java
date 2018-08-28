package engine.parser.tokenization;

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

	public String getValue() {
		return value;
	}

	public TokenType getType() {
		return type;
	}

	public int getLine() {
		return line;
	}

	public String toString() {
		return type.toString() + ": " + value;
	}
}
