package engine.parser.constants;

import engine.parser.tokenization.Token;

public enum TokenConstants {

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% KeyWords
	// -------------------------------------------------- Constructors
	ATTRIBUTE   (TokenType.KEYWORD, "Attribute"),
	TILE        (TokenType.KEYWORD, "Tile"),
	ENTITY      (TokenType.KEYWORD, "Entity"),
	CREATURE    (TokenType.KEYWORD, "Creature"),
	SCRIPT      (TokenType.KEYWORD, "Script"),
	PROCESS     (TokenType.KEYWORD, "Process"),
	DRIVE       (TokenType.KEYWORD, "Drive"),

	// -------------------------------------------------- Scripts
	ON          (TokenType.KEYWORD, "on"),

	// -------------------------------------------------- Structure
	IF          (TokenType.KEYWORD, "if"),
	ELSE        (TokenType.KEYWORD, "else"),
	FOR         (TokenType.KEYWORD, "for"),
	WHILE       (TokenType.KEYWORD, "while"),

	// -------------------------------------------------- Commands
	COMMAND_PRINT   (TokenType.KEYWORD, "print"),
	COMMAND_RANDOM  (TokenType.KEYWORD, "random"),

	// -------------------------------------------------- Engine Values
	VALUE_NAME                  (TokenType.KEYWORD, "name"),
	VALUE_COLOR                 (TokenType.KEYWORD, "color"),
	VALUE_PREFERREDHEIGHT       (TokenType.KEYWORD, "preferredHeight"),
	VALUE_PREFERREDHEIGHTBLUR   (TokenType.KEYWORD, "preferredHeightBlur"),
	VALUE_MESH                  (TokenType.KEYWORD, "mesh"),

	// -------------------------------------------------- List Instantiaters
	VALUES_KNOWLEDGE        (TokenType.KEYWORD, "knowledge"),
	VALUES_DRIVES           (TokenType.KEYWORD, "drives"),
	VALUES_ATTRIBUTES       (TokenType.KEYWORD, "attributes"),
	VALUES_SOLUTIONS        (TokenType.KEYWORD, "solutions"),
	VALUES_ALTERNATIVES     (TokenType.KEYWORD, "alternatives"),

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
	PLUS        (TokenType.OPERATOR, "+", 3),
	MINUS       (TokenType.OPERATOR, "-", 3),
	TIMES       (TokenType.OPERATOR, "*", 2),
	DIVIDE      (TokenType.OPERATOR, "/", 2),
	MODULO      (TokenType.OPERATOR, "%", 2),
	POWER       (TokenType.OPERATOR, "^", 1),

	// -------------------------------------------------- Single Step
	SINGLE_INCREMENT    (TokenType.OPERATOR, "++"),
	SINGLE_DECREMENT    (TokenType.OPERATOR, "--"),

	// -------------------------------------------------- Quick Math
	QUICK_PLUS          (TokenType.OPERATOR, "+=", 8),
	QUICK_MINUS         (TokenType.OPERATOR, "-=", 8),
	QUICK_TIMES         (TokenType.OPERATOR, "*=", 8),
	QUICK_DIVIDE        (TokenType.OPERATOR, "/=", 8),
	QUICK_MODULO        (TokenType.OPERATOR, "%=", 8),
	QUICK_POWER         (TokenType.OPERATOR, "^=", 8),

	// -------------------------------------------------- Comparators
	EQUAL           (TokenType.OPERATOR, "==", 5),
	UNEQUAL         (TokenType.OPERATOR, "!=", 5),
	GREATER         (TokenType.OPERATOR, ">", 4),
	LESSER          (TokenType.OPERATOR, "<", 4),
	GREATER_EQUAL   (TokenType.OPERATOR, ">=", 4),
	LESSER_EQUAL    (TokenType.OPERATOR, "<=", 4),

	// -------------------------------------------------- Logical
	NOT         (TokenType.OPERATOR, "!"),
	AND         (TokenType.OPERATOR, "&&", 6),
	OR          (TokenType.OPERATOR, "||", 7),

	// -------------------------------------------------- Assignment
	ASSIGNMENT  (TokenType.OPERATOR, "=", 8),

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Comments
	COMMENT         (TokenType.COMMENT, "//"),
	COMMENT_OPEN    (TokenType.COMMENT, "/*"),
	COMMENT_CLOSE   (TokenType.COMMENT, "*/"),

	;

	// ###################################################################################
	// ################################ Functionality ####################################
	// ###################################################################################

	private Token token;
	private int precedence = 0;

	TokenConstants(TokenType type, String value) {
		token = new Token(type, value, 0);
	}

	TokenConstants(TokenType type, String value, int precedence) {
		token = new Token(type, value, 0);
		this.precedence = precedence;
	}

	/**
	 * Checks whether the requested TokenConstant is exactly equal to the two given chars.
	 * You have to pass the codepoints, which you may also retrieve with writing the char in single quotes. Eg. 'c'.
	 * @param codePoint1 first char
	 * @param codePoint2 second char
	 * @return true if the tokenConstant equals the two given chars
	 */
	public boolean equals(int codePoint1, int codePoint2) {
		String value = token.getValue();
		if (value.length() == 2) {
			return (value.codePointAt(0) == codePoint1) && (value.codePointAt(1) == codePoint2);
		}
		return false;
	}

	/**
	 * Checks whether this TokenConstant is exaclty equal to the given char.
	 * @param c char to check against
	 * @return true if they are equal
	 */
	public boolean equals(char c) {
		String value = token.getValue();
		if (value.length() == 1) {
			return (value.charAt(0) == c);
		}
		return false;
	}

	/**
	 * This method checks for equality of type and value.
	 * If you only need equality of value, use the method equals(Token) to do so.
	 * @param type to check against
	 * @param value to check against
	 * @return true if both are equal to the requested TokenConstant
	 */
	public boolean equals(TokenType type, String value) {
		return (getType() == type) && (getValue().equals(value));
	}

	/**
	 * This method only checks for equality of String. The Type is not considered here.
	 * Use the method equals(TokenType, String) for verbose equality if necessary.
	 * @param t token whose value to check against
	 * @return true if the values are equal
	 */
	public boolean equals(Token t) {
		return (getValue().equals(t.getValue()));
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

	public int getPrecedence() {
		return precedence;
	}

	/**
	 * Searches the token constants for a token with type KEYWORD and the same value as the given token and returns it.
	 * @param t token to check for
	 * @return token constant with type KEYWORD, if found
	 */
	public static TokenConstants getCorrespondingKeyWord(Token t) {
		for (TokenConstants constant : values()) {
			if (constant.getType() == TokenType.KEYWORD && constant.equals(t)) {
				return constant;
			}
		}
		return null;
	}

	/**
	 * Searches the token constants for a token with the same value as the given token and returns it.
	 * @param t token to check for
	 * @return token constant with same value, if found
	 */
	public static TokenConstants getCorrespondingConstant(Token t) {
		for (TokenConstants constant : values()) {
			if (constant.equals(t)) {
				return constant;
			}
		}
		return null;
	}
}
