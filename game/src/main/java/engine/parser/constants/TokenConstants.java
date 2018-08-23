package engine.parser.constants;

import engine.parser.tokenization.Token;

public enum TokenConstants {

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% KeyWords
	// -------------------------------------------------- Constructors
	ATTRIBUTE   (TokenType.KEYWORD, "Attribute"),
	TILE        (TokenType.KEYWORD, "Tile"),
	ENTITY      (TokenType.KEYWORD, "Instance"),
	SCRIPT      (TokenType.KEYWORD, "Script"),
	PROCESS     (TokenType.KEYWORD, "Process"),

	// -------------------------------------------------- List Instantiaters
	KNOWLEDGE   (TokenType.KEYWORD, "knowledge"),
	ATTRIBUTES  (TokenType.KEYWORD, "attributes"),

	// -------------------------------------------------- Scripts
	ON          (TokenType.KEYWORD, "on"),

	// -------------------------------------------------- Structure
	IF          (TokenType.KEYWORD, "if"),
	ELSE        (TokenType.KEYWORD, "else"),
	FOR         (TokenType.KEYWORD, "for"),
	WHILE       (TokenType.KEYWORD, "while"),

	// -------------------------------------------------- Commands
	PRINT       (TokenType.KEYWORD, "print"),

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Seperators
	// -------------------------------------------------- Brackets
	ROUND_BRACKETS_OPEN     (TokenType.SEPARATOR, "("),
	ROUND_BRACKETS_CLOSE    (TokenType.SEPARATOR, ")"),
	CURLY_BRACKETS_OPEN     (TokenType.SEPARATOR, "{"),
	CURLY_BRACKETS_CLOSE    (TokenType.SEPARATOR, "}"),
	SQUARE_BRACKETS_OPEN    (TokenType.SEPARATOR, "["),
	SQUARE_BRACKETS_CLOSE   (TokenType.SEPARATOR, "]"),

	// -------------------------------------------------- Punctuation Marks
	COMMA       (TokenType.SEPARATOR, ","),
	POINT       (TokenType.SEPARATOR, "."),
	SEMICOLON   (TokenType.SEPARATOR, ";"),
	COLON       (TokenType.SEPARATOR, ":"),

	// -------------------------------------------------- White Space
	WHITE_SPACE (TokenType.SEPARATOR, " "),

	// -------------------------------------------------- Quotation Marks
	QUOTATION_MARK          (TokenType.SEPARATOR, "\""),

	// -------------------------------------------------- Escape Character
	BACK_SLASH  (TokenType.SEPARATOR, "\\"),

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Operators
	// -------------------------------------------------- Math
	PLUS        (TokenType.OPERATOR, "+"),
	MINUS       (TokenType.OPERATOR, "-"),
	TIMES       (TokenType.OPERATOR, "*"),
	DIVIDE      (TokenType.OPERATOR, "/"),
	MODULO      (TokenType.OPERATOR, "%"),
	POWER       (TokenType.OPERATOR, "^"),

	// -------------------------------------------------- Comparators
	EQUAL           (TokenType.OPERATOR, "=="),
	UNEQUAL         (TokenType.OPERATOR, "!="),
	GREATER         (TokenType.OPERATOR, ">"),
	LESSER          (TokenType.OPERATOR, "<"),
	GREATER_EQUAL   (TokenType.OPERATOR, ">="),
	LESSER_EQUAL    (TokenType.OPERATOR, "<="),

	// -------------------------------------------------- Logical
	NOT         (TokenType.OPERATOR, "!"),
	AND         (TokenType.OPERATOR, "&&"),
	OR          (TokenType.OPERATOR, "||"),

	// -------------------------------------------------- Logical
	ASSIGNMENT  (TokenType.OPERATOR, "="),

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Comments
	COMMENT         (TokenType.COMMENT, "//"),
	COMMENT_OPEN    (TokenType.COMMENT, "/*"),
	COMMENT_CLOSE   (TokenType.COMMENT, "*/"),

	;

	// ###################################################################################
	// ################################ Functionality ####################################
	// ###################################################################################

	private Token token;

	TokenConstants(TokenType type, String value) {
		token = new Token(type, value);
	}

	public boolean equals(int codePoint1, int codePoint2) {
		String value = token.getValue();
		if (value.length() >= 2) {
			return (value.codePointAt(0) == codePoint1) && (value.codePointAt(1) == codePoint2);
		} else {
			return false;
		}
	}

	public boolean equals(TokenType type, String value) {
		return (getType() == type) && (getValue().equals(value));
	}

	public boolean equals(Token t) {
		return (getType() == t.getType()) && (getValue().equals(t.getValue()));
	}

	// ###################################################################################
	// ################################ Getters ##########################################
	// ###################################################################################

	public Token getToken() {
		return token;
	}

	public TokenType getType() {
		return token.getType();
	}

	public String getValue() {
		return token.getValue();
	}
}
