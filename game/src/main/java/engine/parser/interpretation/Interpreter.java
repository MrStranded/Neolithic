package engine.parser.interpretation;

import engine.data.ContainerIdentifier;
import engine.data.attributes.PreAttribute;
import engine.data.proto.*;
import engine.data.variables.DataType;
import engine.graphics.renderer.color.RGBA;
import engine.parser.Logger;
import engine.parser.constants.TokenConstants;
import engine.parser.constants.TokenType;
import engine.parser.tokenization.Token;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Interpreter {

	private Iterator<Token> tokenIterator;

	public Interpreter(List<Token> tokens) {
		tokenIterator = tokens.iterator();
	}

	// ###################################################################################
	// ################################ Token Consumption ################################
	// ###################################################################################

	private Token consume(TokenConstants tokenConstant) throws Exception {
		if (!tokenIterator.hasNext()) { // error reporting
			throw new Exception("Reached unexpected end of file!");
		}

		Token next = tokenIterator.next();
		if (tokenConstant.equals(next)) {
			return next;

		} else { // error reporting
			String errorMessage = "Wrong Token! Expected " + tokenConstant.getValue()
					+ " but found " + next.getValue()
					+ " on line " + next.getLine();

			Logger.error(errorMessage);
			throw new Exception(errorMessage);

		}
	}

	private Token consume() throws Exception {
		if (!tokenIterator.hasNext()) { // error reporting
			throw new Exception("Reached unexpected end of file!");
		}

		return tokenIterator.next();
	}

	// ###################################################################################
	// ################################ Data Type Assurance and Conversion ###############
	// ###################################################################################

	/**
	 * Checks whether the given token is a double value (isDouble == true) or an integer value (isDouble == false).
	 * If not, an error report is issued.
	 * @param token to check
	 * @param isDouble allow one decimal point or not
	 * @return true if the value is legit
	 */
	private boolean isNumber(Token token, boolean isDouble) {
		if (token == null) {
			return false; // exit without error
		}

		if (token.getType() == TokenType.LITERAL) {
			boolean isNumber = true;
			boolean hadPoint = false;
			for (char c : token.getValue().toCharArray()) {
				if (!Character.isDigit(c)) {
					if (TokenConstants.POINT.equals(c) && !hadPoint && isDouble) {
						hadPoint = true;
					} else {
						isNumber = false;
						break;
					}
				}
			}

			if (isNumber) {
				return true;
			}
		}
		if (isDouble) {
			Logger.error("Expected floating point number value but got '" + token.getValue() + "' on line " + token.getLine());
		} else {
			Logger.error("Expected integer number value but got '" + token.getValue() + "' on line " + token.getLine());
		}
		return false;
	}

	/**
	 * Returns the token's value as an integer if possible.
	 * Otherwise it issues an error log and returns 0.
	 * @param token to convert
	 * @return the token's value or zero
	 */
	private int getInt(Token token) {
		return isNumber(token, false)? Integer.parseInt(token.getValue()) : 0;
	}

	/**
	 * Returns the token's value as an double if possible.
	 * Otherwise it issues an error log and returns 0.
	 * @param token to convert
	 * @return the token's value or zero
	 */
	private double getDouble(Token token) {
		return isNumber(token, true)? Double.parseDouble(token.getValue()) : 0;
	}

	// ###################################################################################
	// ################################ Top Level  #######################################
	// ###################################################################################

	public void interpret() throws Exception {
		// outer most level of a script
		while (tokenIterator.hasNext()) {
			Token next = consume();

			if (TokenConstants.ATTRIBUTE.equals(next)) {        // Attribute
				createAttribute();
			} else if (TokenConstants.TILE.equals(next)) {      // Tile
				createEntity(DataType.TILE);
			} else if (TokenConstants.CREATURE.equals(next)) {  // Creature
				createEntity(DataType.CREATURE);
			} else if (TokenConstants.ENTITY.equals(next)) {    // Entity
				createEntity(DataType.ENTITY);
			} else if (TokenConstants.DRIVE.equals(next)) {     // Drive
				createEntity(DataType.DRIVE);
			} else if (TokenConstants.PROCESS.equals(next)) {     // Process
				createEntity(DataType.PROCESS);
			}
		}
	}

	// ###################################################################################
	// ################################ Lists ############################################
	// ###################################################################################

	/**
	 * A TokenConstants.VALUES_ATTRIBUTES has been encountered. So let's add some Attributes to the container, shall we?
	 * @param container to add the attributes to
	 * @throws Exception
	 */
	private void addAttributes(Container container) throws Exception {
		// attributes { attOne, 10; attTwo, 7; }
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		Token nextToken;
		while (!TokenConstants.CURLY_BRACKETS_CLOSE.equals(nextToken = consume())) {
			consume(TokenConstants.COMMA);
			Token token = consume();
			consume(TokenConstants.SEMICOLON);

			PreAttribute preAttribute = new PreAttribute(nextToken.getValue(), getInt(token));
			container.addPreAttribute(preAttribute);
		}
	}

	private void feedTextIDList(List<ContainerIdentifier> identifiers) throws Exception {
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		Token nextToken;
		while (!TokenConstants.CURLY_BRACKETS_CLOSE.equals(nextToken = consume())) {
			consume(TokenConstants.SEMICOLON);

			if (nextToken.getValue() != null && nextToken.getValue().length() > 0) {
				identifiers.add(new ContainerIdentifier(nextToken.getValue()));
			}
		}
	}

	// ###################################################################################
	// ################################ Value Helper Functions ###########################
	// ###################################################################################

	// ################################################################################### General

	private void addName(Container container) throws Exception {
		consume(TokenConstants.ASSIGNMENT);

		Token name = consume();
		container.setName(name.getValue());

		consume(TokenConstants.SEMICOLON);
	}

	private void addName(ProtoAttribute protoAttribute) throws Exception {
		consume(TokenConstants.ASSIGNMENT);

		Token name = consume();
		protoAttribute.setName(name.getValue());

		consume(TokenConstants.SEMICOLON);
	}

	// ################################################################################### Tile

	private void readPreferredHeight(TileContainer container) throws Exception {
		consume(TokenConstants.ASSIGNMENT);

		Token height = consume();
		container.setPreferredHeight(getInt(height));

		Token nextSub = consume();
		if (TokenConstants.COMMA.equals(nextSub)) {
			Token blur = consume();
			container.setPreferredHeightBlur(getInt(blur));

			consume(TokenConstants.SEMICOLON);

		} else if (TokenConstants.SEMICOLON.equals(nextSub)) {
			return;
		} else {
			Logger.error("Expected '" + TokenConstants.SEMICOLON.getValue() + "' but got '" + nextSub + "' on line " + nextSub.getLine());
		}
	}

	private void readPreferredHeightBlur(TileContainer container) throws Exception {
		consume(TokenConstants.ASSIGNMENT);

		Token blur = consume();
		container.setPreferredHeightBlur(getInt(blur));

		consume(TokenConstants.SEMICOLON);
	}

	private void readColor(TileContainer container) throws Exception {
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		Token seperator;
		Token[][] values = new Token[3][2]; // x axis: r,g,b | y axis: color value, deviation

		for (int i=0; i<3; i++) {
			values[i][0] = consume();
			seperator = consume();
			if (TokenConstants.COMMA.equals(seperator)) {
				values[i][1] = consume();
				consume(TokenConstants.SEMICOLON);
			}
		}

		container.setColor(new RGBA(
				getDouble(values[0][0]) / 255d,
				getDouble(values[1][0]) / 255d,
				getDouble(values[2][0]) / 255d
		));
		container.setColorDeviation(new RGBA(
				getDouble(values[0][1]) / 255d,
				getDouble(values[1][1]) / 255d,
				getDouble(values[2][1]) / 255d
		));

		consume(TokenConstants.CURLY_BRACKETS_CLOSE);
	}

	// ###################################################################################
	// ################################ Attribute ########################################
	// ###################################################################################

	private void createAttribute() throws Exception {
		// Attribute : attTextId { ... }
		consume(TokenConstants.COLON);
		Token textId = consume();
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		ProtoAttribute protoAttribute = new ProtoAttribute(textId.getValue());
		Data.addProtoAttribute(protoAttribute);

		while (true) {
			Token next = consume();
			if (TokenConstants.VALUE_NAME.equals(next)) { // name definition
				addName(protoAttribute);

			} else if (TokenConstants.CURLY_BRACKETS_CLOSE.equals(next)) { // end of definition
				return;

			} else { // unknown command
				Logger.error("Unknown Attribute definition command '" + next.getValue() + "' on line " + next.getLine());
			}
		}
	}

	// ###################################################################################
	// ################################ Entity ###########################################
	// ###################################################################################

	private void createEntity(final DataType type) throws Exception {
		consume(TokenConstants.COLON);
		Token textId = consume();
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		// container creation
		Container container;
		switch (type) {
			case ENTITY:
				container = new Container(textId.getValue(), DataType.ENTITY);
				break;
			case TILE:
				container = new TileContainer(textId.getValue());
				break;
			case CREATURE:
				container = new CreatureContainer(textId.getValue());
				break;
			case DRIVE:
				container = new DriveContainer(textId.getValue());
				break;
			case PROCESS:
				container = new ProcessContainer(textId.getValue());
				break;
			default:
				Logger.error("Unknown entity constructor type '" + type + "' for '" + textId.getValue() + "' on line " + textId.getLine());
				return;
		}
		Data.addContainer(container);

		// container filling
		while (true) {
			Token next = consume();
			if (TokenConstants.VALUE_NAME.equals(next)) { // name definition
				addName(container);

			} else if (TokenConstants.VALUE_PREFERREDHEIGHT.equals(next)) { // preferred height definition
				if (type == DataType.TILE) {
					readPreferredHeight((TileContainer) container);
				} else { issueTypeError(next, type); }

			} else if (TokenConstants.VALUE_PREFERREDHEIGHTBLUR.equals(next)) { // preferred height blur definition
				if (type == DataType.TILE) {
					readPreferredHeightBlur((TileContainer) container);
				} else { issueTypeError(next, type); }

			} else if (TokenConstants.VALUE_COLOR.equals(next)) { // color definition
				if (type == DataType.TILE) {
					readColor((TileContainer) container);
				} else { issueTypeError(next, type); }

			} else if (TokenConstants.VALUES_ATTRIBUTES.equals(next)) { // list of attributes
				addAttributes(container);

			} else if (TokenConstants.VALUES_KNOWLEDGE.equals(next)) { // list of known processes
				if (type == DataType.CREATURE) {
					feedTextIDList(((CreatureContainer) container).getKnowledge());
				} else { issueTypeError(next, type); }

			} else if (TokenConstants.VALUES_DRIVES.equals(next)) { // list of drives
				if (type == DataType.CREATURE) {
					feedTextIDList(((CreatureContainer) container).getDrives());
				} else { issueTypeError(next, type); }

			} else if (TokenConstants.VALUES_SOLUTIONS.equals(next)) { // list of solutions
				if (type == DataType.DRIVE) {
					feedTextIDList(((DriveContainer) container).getSolutions());
				} else { issueTypeError(next, type); }

			} else if (TokenConstants.VALUES_ALTERNATIVES.equals(next)) { // list of alternatives
				if (type == DataType.PROCESS) {
					feedTextIDList(((ProcessContainer) container).getAlternatives());
				} else { issueTypeError(next, type); }

			} else if (TokenConstants.CURLY_BRACKETS_CLOSE.equals(next)) { // end of definition
				return;

			} else { // unknown command
				Logger.error("Unknown Entity definition command '" + next.getValue() + "' on line " + next.getLine());
			}
		}
	}

	private void issueTypeError(Token command, DataType type) {
		Logger.error("The command '" + command.getValue() + "' is not applicable to the type " + type + " (line " + command.getLine() + ")");
	}

}
