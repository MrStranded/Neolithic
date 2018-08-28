package engine.parser.interpretation;

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
				createTile();
			} else if (TokenConstants.CREATURE.equals(next)) {  // Creature
				createCreature();
			} else if (TokenConstants.ENTITY.equals(next)) {    // Entity
				createEntity();
			} else if (TokenConstants.DRIVE.equals(next)) {     // Drive
				createDrive();
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

	private void feedTextIDList(List<String> textIDs) throws Exception {
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		Token nextToken;
		while (!TokenConstants.CURLY_BRACKETS_CLOSE.equals(nextToken = consume())) {
			consume(TokenConstants.SEMICOLON);

			if (nextToken.getValue() != null && nextToken.getValue().length() > 0) {
				textIDs.add(nextToken.getValue());
			}
		}
	}

	// ###################################################################################
	// ################################ Value Helper Functions ###########################
	// ###################################################################################

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
	// ################################ Tile #############################################
	// ###################################################################################

	private void createTile() throws Exception {
		// Tile : tTextId { ... }
		consume(TokenConstants.COLON);
		Token textId = consume();
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		TileContainer protoTile = new TileContainer(textId.getValue());
		Data.addContainer(protoTile);

		while (true) {
			Token next = consume();
			if (TokenConstants.VALUE_NAME.equals(next)) { // name definition
				addName(protoTile);

			} else if (TokenConstants.VALUE_PREFERREDHEIGHT.equals(next)) { // preferred height definition
				consume(TokenConstants.ASSIGNMENT);

				Token height = consume();
				protoTile.setPreferredHeight(getInt(height));

				Token nextSub = consume();
				if (TokenConstants.COMMA.equals(nextSub)) {
					Token blur = consume();
					protoTile.setPreferredHeightBlur(getInt(blur));

					consume(TokenConstants.SEMICOLON);

				} else if (TokenConstants.SEMICOLON.equals(nextSub)) {
					// exit definition
				} else {
					Logger.error("Expected '" + TokenConstants.SEMICOLON.getValue() + "' but got '" + nextSub + "' on line " + nextSub.getLine());
				}

			} else if (TokenConstants.VALUE_PREFERREDHEIGHTBLUR.equals(next)) { // preferred height blur definition
				consume(TokenConstants.ASSIGNMENT);

				Token blur = consume();
				protoTile.setPreferredHeightBlur(getInt(blur));

				consume(TokenConstants.SEMICOLON);

			} else if (TokenConstants.VALUE_COLOR.equals(next)) { // color definition
				consume(TokenConstants.CURLY_BRACKETS_OPEN);

				Token seperator;
				Token redDeviation = null, greenDeviation = null, blueDeviation = null;

				Token red = consume();
				seperator = consume();
				if (TokenConstants.COMMA.equals(seperator)) {
					redDeviation = consume();
					consume(TokenConstants.SEMICOLON);
				}

				Token green = consume();
				seperator = consume();
				if (TokenConstants.COMMA.equals(seperator)) {
					greenDeviation = consume();
					consume(TokenConstants.SEMICOLON);
				}

				Token blue = consume();
				seperator = consume();
				if (TokenConstants.COMMA.equals(seperator)) {
					blueDeviation = consume();
					consume(TokenConstants.SEMICOLON);
				}

				double colorRed = getDouble(red) / 255d;
				double colorGreen = getDouble(green) / 255d;
				double colorBlue = getDouble(blue) / 255d;

				double deviationRed = getDouble(redDeviation) / 255d;
				double deviationGreen = getDouble(greenDeviation) / 255d;
				double deviationBlue = getDouble(blueDeviation) / 255d;

				protoTile.setColor(new RGBA(colorRed, colorGreen, colorBlue));
				protoTile.setColorDeviation(new RGBA(deviationRed, deviationGreen, deviationBlue));

				consume(TokenConstants.CURLY_BRACKETS_CLOSE);

			} else if (TokenConstants.VALUES_ATTRIBUTES.equals(next)) { // list of attributes
				addAttributes(protoTile);

			} else if (TokenConstants.CURLY_BRACKETS_CLOSE.equals(next)) { // end of definition
				return;

			} else { // unknown command
				Logger.error("Unknown Tile definition command '" + next.getValue() + "' on line " + next.getLine());
			}
		}
	}

	// ###################################################################################
	// ################################ Creature #########################################
	// ###################################################################################

	private void createCreature() throws Exception {
		// Creature : cTextId { ... }
		consume(TokenConstants.COLON);
		Token textId = consume();
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		CreatureContainer protoCreature = new CreatureContainer(textId.getValue());
		Data.addContainer(protoCreature);

		while (true) {
			Token next = consume();
			if (TokenConstants.VALUE_NAME.equals(next)) { // name definition
				addName(protoCreature);

			} else if (TokenConstants.VALUES_ATTRIBUTES.equals(next)) { // list of attributes
				addAttributes(protoCreature);

			} else if (TokenConstants.VALUES_KNOWLEDGE.equals(next)) { // list of known processes
				feedTextIDList(protoCreature.getPreKnownProcesses());

			} else if (TokenConstants.VALUES_DRIVES.equals(next)) { // list of drives
				feedTextIDList(protoCreature.getPreDrives());

			} else if (TokenConstants.CURLY_BRACKETS_CLOSE.equals(next)) { // end of definition
				return;

			} else { // unknown command
				Logger.error("Unknown Creature definition command '" + next.getValue() + "' on line " + next.getLine());
			}
		}
	}

	// ###################################################################################
	// ################################ Entity ###########################################
	// ###################################################################################

	private void createEntity() throws Exception {
		// Entity : eTextId { ... }
		consume(TokenConstants.COLON);
		Token textId = consume();
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		Container protoEntity = new Container(textId.getValue(), DataType.ENTITY);
		Data.addContainer(protoEntity);

		while (true) {
			Token next = consume();
			if (TokenConstants.VALUE_NAME.equals(next)) { // name definition
				addName(protoEntity);

			} else if (TokenConstants.VALUES_ATTRIBUTES.equals(next)) { // list of attributes
				addAttributes(protoEntity);

			} else if (TokenConstants.CURLY_BRACKETS_CLOSE.equals(next)) { // end of definition
				return;

			} else { // unknown command
				Logger.error("Unknown Entity definition command '" + next.getValue() + "' on line " + next.getLine());
			}
		}
	}

	// ###################################################################################
	// ################################ Drive ############################################
	// ###################################################################################

	private void createDrive() throws Exception {
		// Entity : eTextId { ... }
		consume(TokenConstants.COLON);
		Token textId = consume();
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		DriveContainer protoDrive = new DriveContainer(textId.getValue());
		Data.addContainer(protoDrive);

		while (true) {
			Token next = consume();
			if (TokenConstants.VALUE_NAME.equals(next)) { // name definition
				addName(protoDrive);

			} else if (TokenConstants.VALUES_SOLUTIONS.equals(next)) { // list of solutions
				feedTextIDList(protoDrive.getPreSolutions());

			} else if (TokenConstants.CURLY_BRACKETS_CLOSE.equals(next)) { // end of definition
				return;

			} else { // unknown command
				Logger.error("Unknown Drive definition command '" + next.getValue() + "' on line " + next.getLine());
			}
		}
	}

}
