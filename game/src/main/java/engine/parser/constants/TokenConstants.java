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

	// -------------------------------------------------- Scripts
	ON          (TokenType.KEYWORD, "on"),

	// -------------------------------------------------- Structure
	IF          (TokenType.KEYWORD, "if"),
	ELSE        (TokenType.KEYWORD, "else"),
	FOR         (TokenType.KEYWORD, "for"),
	WHILE       (TokenType.KEYWORD, "while"),

	// -------------------------------------------------- Commands
	PRINT       (TokenType.KEYWORD, "print"),

	// -------------------------------------------------- Engine Values
	VALUE_NAME                  (TokenType.KEYWORD, "name"),
	VALUE_COLOR                 (TokenType.KEYWORD, "color"),
	VALUE_PREFERREDHEIGHT       (TokenType.KEYWORD, "preferredHeight"),
	VALUE_PREFERREDHEIGHTBLUR   (TokenType.KEYWORD, "preferredHeightBlur"),

	// -------------------------------------------------- List Instantiaters
	VALUES_KNOWLEDGE   (TokenType.KEYWORD, "knowledge"),
	VALUES_ATTRIBUTES  (TokenType.KEYWORD, "attributes"),

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
		token = new Token(type, value, 0);
	}

	public boolean equals(int codePoint1, int codePoint2) {
		String value = token.getValue();
		if (value.length() >= 2) {
			return (value.codePointAt(0) == codePoint1) && (value.codePointAt(1) == codePoint2);
		}
		return false;
	}

	public boolean equals(char c) {
		String value = token.getValue();
		if (value.length() == 1) {
			return (value.charAt(0) == c);
		}
		return false;
	}

	public boolean equals(TokenType type, String value) {
		return (getType() == type) && (getValue().equals(value));
	}

	public boolean equals(Token t) {
		return (getValue().equals(t.getValue()));
	}

	/**
	 * I needed to change the name of the method, so the jvm knew which method to use.
	 * (It used the default equals method for some reason)
	 * @param tokenValue String value of asked token
	 * @return true if the string values are equal
	 */
	public boolean isEqualTo(String tokenValue) {
		return getValue().equals(tokenValue);
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
