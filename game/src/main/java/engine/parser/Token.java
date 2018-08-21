package engine.parser;

import engine.parser.constants.TokenType;

public class Token {

	private String value;
	private TokenType type;

	public Token(TokenType type, String value) {
		this.type = type;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public TokenType getType() {
		return type;
	}

	public String toString() {
		return type.toString() + ": " + value;
	}
}
