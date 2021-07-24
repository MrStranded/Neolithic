package engine.parser.constants;

import engine.parser.tokenization.Token;

public enum TokenConstants {

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% KeyWords
	// -------------------------------------------------- Constructors
	ATTRIBUTE   (TokenType.KEYWORD, "Attribute"),
	TILE        (TokenType.KEYWORD, "Tile"),
	ENTITY      (TokenType.KEYWORD, "Entity"),
    EFFECT      (TokenType.KEYWORD, "Effect"),
	CREATURE    (TokenType.KEYWORD, "Creature"),
	SCRIPT      (TokenType.KEYWORD, "Script"),
	PROCESS     (TokenType.KEYWORD, "Process"),
	DRIVE       (TokenType.KEYWORD, "Drive"),
	FORMATION   (TokenType.KEYWORD, "Formation"),
	WORLDGEN    (TokenType.KEYWORD, "WorldGen"),
	GUI		    (TokenType.KEYWORD, "Gui"),
	STAGE	    (TokenType.KEYWORD, "Stage"),

	// -------------------------------------------------- Inheritance
	INHERITS	(TokenType.KEYWORD, "inherits"),

	// -------------------------------------------------- Scripts
	SELF        (TokenType.KEYWORD, "self"),
    MAIN        (TokenType.KEYWORD, "main"),

	// -------------------------------------------------- Values
	TRUE        (TokenType.KEYWORD, "true"),
	FALSE       (TokenType.KEYWORD, "false"),
	PI	     	(TokenType.KEYWORD, "PI"),
	TAU	     	(TokenType.KEYWORD, "TAU"),

	// -------------------------------------------------- Structure
	IF          (TokenType.KEYWORD, "if"),
	ELSE        (TokenType.KEYWORD, "else"),
	FOR         (TokenType.KEYWORD, "for"),
	WHILE       (TokenType.KEYWORD, "while"),
    BREAK       (TokenType.KEYWORD, "break"),
    RETURN      (TokenType.KEYWORD, "return"),

	// -------------------------------------------------- Attributes
	VALUE_NAME             		(TokenType.KEYWORD, "name"),
	VALUE_INHERITED             (TokenType.KEYWORD, "inherited"),
	VALUE_MUTATION_CHANCE       (TokenType.KEYWORD, "mutationChance"),
	VALUE_MUTATION_EXTEND       (TokenType.KEYWORD, "mutationExtent"),
	VALUE_MUTATION              (TokenType.KEYWORD, "mutation"),
	VALUE_LOWER_BOUND           (TokenType.KEYWORD, "lower"),
	VALUE_UPPER_BOUND           (TokenType.KEYWORD, "upper"),
	VALUE_BOUNDS              	(TokenType.KEYWORD, "bounds"),
	VALUE_GUI_COLOR             (TokenType.KEYWORD, "color"),

	// -------------------------------------------------- List Instantiators
	VALUES_KNOWLEDGE        (TokenType.KEYWORD, "knowledge"),
	VALUES_DRIVES           (TokenType.KEYWORD, "drives"),
	VALUES_ATTRIBUTES       (TokenType.KEYWORD, "attributes"),
    VALUES_SOLUTIONS        (TokenType.KEYWORD, "solutions"),

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Commands
	// -------------------------------------------------- Math
	CHANCE          (TokenType.COMMAND, "chance"),
	RANDOM          (TokenType.COMMAND, "random"),
	ABSOLUTE        (TokenType.COMMAND, "abs"),
	MAX				(TokenType.COMMAND, "max"),
	MIN				(TokenType.COMMAND, "min"),
	FLOOR			(TokenType.COMMAND, "floor"),
	CEIL			(TokenType.COMMAND, "ceil"),
	PERCENT			(TokenType.COMMAND, "percent"),
	COS				(TokenType.COMMAND, "cos"),
	SIN				(TokenType.COMMAND, "sin"),
	TAN				(TokenType.COMMAND, "tan"),
	ACOS			(TokenType.COMMAND, "acos"),
	ATAN			(TokenType.COMMAND, "atan"),
	ATAN2			(TokenType.COMMAND, "atan2"),

	// -------------------------------------------------- Entity Manipulation
	CHANGE                  (TokenType.COMMAND, "change"),
	CREATE                  (TokenType.COMMAND, "create"),
	GET_OR_CREATE           (TokenType.COMMAND, "getOrCreate"),
	DESTROY                 (TokenType.COMMAND, "destroy"),
	DELETE_EFFECTS			(TokenType.COMMAND, "deleteEffects"),
	ADD_EFFECT				(TokenType.COMMAND, "addEffect"),
	ADD_PERSONAL_ATTRIBUTE  (TokenType.COMMAND, "addPersonalAtt"),
	ADD_SPECIES_ATTRIBUTE   (TokenType.COMMAND, "addSpeciesAtt"),
	ADD_OCCUPATION			(TokenType.COMMAND, "addOccupation"),
	MIX_ATTRIBUTES          (TokenType.COMMAND, "mixAttributes"),
	SET_MESH				(TokenType.COMMAND, "setMesh"),
	DELAY_NEXT_TICK			(TokenType.COMMAND, "delayNextTick"),
	SELECT					(TokenType.COMMAND, "select"),

	// -------------------------------------------------- Planet Manipulation
    CHANGE_SUN_ANGLE    (TokenType.COMMAND, "changeSunAngle"),
	CREATE_FORMATION    (TokenType.COMMAND, "createFormation"),
	FIT_TILES           (TokenType.COMMAND, "fitTiles"),
	SET_HEIGHT          (TokenType.COMMAND, "setHeight"),
	SET_LEVEL     		(TokenType.COMMAND, "setLevel"),
	SET_WATER_HEIGHT    (TokenType.COMMAND, "setWaterHeight"),
	SET_WATER_LEVEL     (TokenType.COMMAND, "setWaterLevel"),
    SET_SUN_ANGLE       (TokenType.COMMAND, "setSunAngle"),
	UPDATE_PLANET_MESH	(TokenType.COMMAND, "updatePlanetMesh"),

	// -------------------------------------------------- Retrieval
	GET_ATTRIBUTE           	(TokenType.COMMAND, "getAtt"),
	GET_ATTRIBUTE_IN_RANGE  	(TokenType.COMMAND, "getAttInRange"),
	GET_ATTRIBUTES_IN_RANGE 	(TokenType.COMMAND, "getAttsInRange"),
	GET_INSTANCE				(TokenType.COMMAND, "getInstance"),
	GET_INSTANCE_IN_RANGE		(TokenType.COMMAND, "getInstanceInRange"), // deprecated
    GET_INSTANCES_IN_RANGE		(TokenType.COMMAND, "getInstancesInRange"),
	GET_CREATURES_IN_RANGE		(TokenType.COMMAND, "getCreaturesInRange"),
	GET_EFFECT              	(TokenType.COMMAND, "getEffect"),
    GET_EFFECTS             	(TokenType.COMMAND, "getEffects"),
	GET_FULL_ATTRIBUTE      	(TokenType.COMMAND, "getFullAtt"),
	GET_HEIGHT              	(TokenType.COMMAND, "getHeight"),
	GET_HOLDER              	(TokenType.COMMAND, "getHolder"),
	GET_ITEM_ATTRIBUTE      	(TokenType.COMMAND, "getItemAtt"),
	GET_ITEMS					(TokenType.COMMAND, "getItems"),
	GET_LATITUDE    	     	(TokenType.COMMAND, "getLatitude"),
	GET_LONGITUDE	         	(TokenType.COMMAND, "getLongitude"),
	GET_LIGHT_LEVEL         	(TokenType.COMMAND, "getLightLevel"),
	GET_NAME         			(TokenType.COMMAND, "getName"),
	GET_NEIGHBOUR           	(TokenType.COMMAND, "getNeighbor"), // difference between british english in code and american english in scripts
	GET_NEIGHBOURS          	(TokenType.COMMAND, "getNeighbors"),
	GET_PROPERTY        		(TokenType.COMMAND, "getProperty"),
	GET_TILE                	(TokenType.COMMAND, "getTile"),
	GET_TILE_FROM_COORDINATES	(TokenType.COMMAND, "getTileFromCoords"),
	GET_TILES_IN_RANGE      	(TokenType.COMMAND, "getTilesInRange"),
	GET_TILES_OF_PATH			(TokenType.COMMAND, "getTilesOfPath"),
	GET_TYPE					(TokenType.COMMAND, "getType"),
	GET_WATER_HEIGHT        	(TokenType.COMMAND, "getWaterHeight"),
	HAS_EFFECT              	(TokenType.COMMAND, "hasEffect"),
	IS_CLOSER_TO_EQUATOR    	(TokenType.COMMAND, "isCloserToEquator"),
	IS_FARTHER_NORTH    		(TokenType.COMMAND, "isFartherNorth"),
	IS_FARTHER_SOUTH    		(TokenType.COMMAND, "isFartherSouth"),
	IS_NEIGHBOUR            	(TokenType.COMMAND, "isNeighbor"),
	IS_ON_FLOOR					(TokenType.COMMAND, "isOnFloor"),
	IS_SELECTED					(TokenType.COMMAND, "isSelected"),
	RANDOM_TILE             	(TokenType.COMMAND, "randomTile"),

	// -------------------------------------------------- Stages
	GET_STAGE				(TokenType.COMMAND, "getStage"),
	IN_STAGE				(TokenType.COMMAND, "inStage"),
	IN_DEFAULT_STAGE		(TokenType.COMMAND, "inDefaultStage"),
	SET_STAGE				(TokenType.COMMAND, "setStage"),
	SET_DEFAULT_STAGE		(TokenType.COMMAND, "setDefaultStage"),

	// -------------------------------------------------- Actions
	SET_AT          (TokenType.COMMAND, "setAt"),
	MOVE_TO         (TokenType.COMMAND, "moveTo"),
	PICK_UP         (TokenType.COMMAND, "pickUp"),

	// -------------------------------------------------- Variable Manipulation
	FREE_VARIABLE			(TokenType.COMMAND, "forget"),

	// -------------------------------------------------- System
	GET_MEMORY_ADDRESS		(TokenType.COMMAND, "getMemoryAddress"),
	BREAKPOINT				(TokenType.COMMAND, "breakpoint"),
	DEBUG					(TokenType.COMMAND, "debug"),

	// -------------------------------------------------- GUI
	ADD_GUI      	(TokenType.COMMAND, "addGui"),
	CLEAR_GUI		(TokenType.COMMAND, "clearGui"),
	CREATE_GUI      (TokenType.COMMAND, "createGui"),
	PRINT           (TokenType.COMMAND, "print"),

	// -------------------------------------------------- Return Commands
	REQUIRE         (TokenType.COMMAND, "require"),

    // -------------------------------------------------- Lists
    LENGTH          (TokenType.COMMAND, "length"),
	CONTAINS		(TokenType.COMMAND, "contains"),

	// -------------------------------------------------- Iterators
	EACH_ATTRIBUTE  (TokenType.COMMAND, "eachAttribute"),
	EACH_TILE       (TokenType.COMMAND, "eachTile"),
	EACH_ENTITY     (TokenType.COMMAND, "eachEntity"),
	EACH_CREATURE   (TokenType.COMMAND, "eachCreature"),

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Property Type Modifiers
	RGB        		(TokenType.COMMAND, "rgb"),
	RGBA        	(TokenType.COMMAND, "rgba"),
	COLOR        	(TokenType.COMMAND, "color"),
	MESH_PATH       (TokenType.COMMAND, "mesh"),

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Separators
	// -------------------------------------------------- Brackets
	ROUND_BRACKETS_OPEN     (TokenType.SEPARATOR, "("),
	ROUND_BRACKETS_CLOSE    (TokenType.SEPARATOR, ")"),
	CURLY_BRACKETS_OPEN     (TokenType.SEPARATOR, "{"),
	CURLY_BRACKETS_CLOSE    (TokenType.SEPARATOR, "}"),
	SQUARE_BRACKETS_OPEN    (TokenType.SEPARATOR, "["),
	SQUARE_BRACKETS_CLOSE   (TokenType.SEPARATOR, "]"),

	// -------------------------------------------------- Punctuation Marks
	COMMA       (TokenType.SEPARATOR, ","),
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
	PLUS        (TokenType.OPERATOR, "+", 4),
	MINUS       (TokenType.OPERATOR, "-", 4),
	TIMES       (TokenType.OPERATOR, "*", 3),
	DIVIDE      (TokenType.OPERATOR, "/", 3),
	MODULO      (TokenType.OPERATOR, "%", 3),
	POWER       (TokenType.OPERATOR, "^", 2),

	// -------------------------------------------------- Single Step
	SINGLE_INCREMENT    (TokenType.OPERATOR, "++",1),
	SINGLE_DECREMENT    (TokenType.OPERATOR, "--",1),

	// -------------------------------------------------- Quick Math
	QUICK_PLUS          (TokenType.OPERATOR, "+=", 9),
	QUICK_MINUS         (TokenType.OPERATOR, "-=", 9),
	QUICK_TIMES         (TokenType.OPERATOR, "*=", 9),
	QUICK_DIVIDE        (TokenType.OPERATOR, "/=", 9),
	QUICK_MODULO        (TokenType.OPERATOR, "%=", 9),
	QUICK_POWER         (TokenType.OPERATOR, "^=", 9),

	// -------------------------------------------------- Comparators
	EQUAL           (TokenType.OPERATOR, "==", 6),
	UNEQUAL         (TokenType.OPERATOR, "!=", 6),
	GREATER         (TokenType.OPERATOR, ">", 5),
	LESSER          (TokenType.OPERATOR, "<", 5),
	GREATER_EQUAL   (TokenType.OPERATOR, ">=", 5),
	LESSER_EQUAL    (TokenType.OPERATOR, "<=", 5),

	// -------------------------------------------------- Logical
	NOT         (TokenType.OPERATOR, "!",1),
	AND         (TokenType.OPERATOR, "&&", 7),
	OR          (TokenType.OPERATOR, "||", 8),

    // -------------------------------------------------- Bitwise
    SHIFT_LEFT      (TokenType.OPERATOR, "<<",2),
    SHIFT_RIGHT     (TokenType.OPERATOR, ">>",2),
    BITWISE_AND     (TokenType.OPERATOR, "&",2),
    BITWISE_OR      (TokenType.OPERATOR, "|",2),
    BITWISE_XOR     (TokenType.OPERATOR, "^^",2),

	// -------------------------------------------------- Assignment
	ASSIGNMENT  (TokenType.OPERATOR, "=", 9),

	// -------------------------------------------------- Special Operators
	OBJECT_OPERATOR (TokenType.OPERATOR, "->", -1),
	POINT           (TokenType.OPERATOR, ".", 0),
	DOUBLE_POINT    (TokenType.OPERATOR, "..",0),

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
	 * Searches the token constants for a token with the specified type and the same value as the given token and returns it.
	 * @param t token to check for
	 * @param type type to check for
	 * @return token constant with specified type, if found
	 */
	public static TokenConstants getCorrespondingConstantOfType(Token t, TokenType type) {
		for (TokenConstants constant : values()) {
			if (constant.getType() == type && constant.equals(t)) {
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
