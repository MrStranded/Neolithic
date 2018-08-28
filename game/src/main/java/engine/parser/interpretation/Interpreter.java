package engine.parser.interpretation;

import engine.data.attributes.Attribute;
import engine.data.attributes.PreAttribute;
import engine.data.proto.*;
import engine.data.variables.DataType;
import engine.graphics.renderer.color.RGBA;
import engine.parser.Logger;
import engine.parser.constants.TokenConstants;
import engine.parser.constants.TokenType;
import engine.parser.tokenization.Token;
import engine.utils.converters.IntegerConverter;

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

	private String consume(TokenConstants tokenConstant) throws Exception {
		if (!tokenIterator.hasNext()) { // error reporting
			throw new Exception("Reached unexpected end of file!");
		}

		Token next = tokenIterator.next();
		if (tokenConstant.isEqualTo(next.getValue())) {
			return next.getValue();

		} else { // error reporting
			String errorMessage = "Wrong Token! Expected " + tokenConstant.getValue()
					+ " but found " + next.getValue()
					+ " on line " + next.getLine();
			Logger.error(errorMessage);
			/*StringBuilder successive = new StringBuilder();
			int i = 0;
			while (tokenIterator.hasNext() && i < 20) {
				successive.append(tokenIterator.next().getValue());
				successive.append(" ");
				i++;
			}
			Logger.error("Right before: " + successive);*/
			throw new Exception(errorMessage);

		}
	}

	private String consume() throws Exception {
		if (!tokenIterator.hasNext()) { // error reporting
			throw new Exception("Reached unexpected end of file!");
		}

		Token next = tokenIterator.next();
		return next.getValue();
	}

	private Token consumeToken() throws Exception {
		if (!tokenIterator.hasNext()) { // error reporting
			throw new Exception("Reached unexpected end of file!");
		}

		Token next = tokenIterator.next();
		return next;
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
			String next = consume();

			if (TokenConstants.ATTRIBUTE.isEqualTo(next)) {        // Attribute
				createAttribute();
			} else if (TokenConstants.TILE.isEqualTo(next)) {      // Tile
				createTile();
			} else if (TokenConstants.CREATURE.isEqualTo(next)) {  // Creature
				createCreature();
			}
		}
	}

	// ###################################################################################
	// ################################ Attribute ########################################
	// ###################################################################################

	private void createAttribute() throws Exception {
		// Attribute : attTextId { ... }
		consume(TokenConstants.COLON);
		String textId = consume();
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		ProtoAttribute protoAttribute = new ProtoAttribute(textId);
		Data.addProtoAttribute(protoAttribute);

		while (true) {
			String next = consume();
			if (TokenConstants.VALUE_NAME.isEqualTo(next)) { // name definition
				consume(TokenConstants.ASSIGNMENT);

				String name = consume();
				protoAttribute.setName(name);

				consume(TokenConstants.SEMICOLON);

			} else if (TokenConstants.CURLY_BRACKETS_CLOSE.isEqualTo(next)) { // end of definition
				return;

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

		String nextSub;
		while (!TokenConstants.CURLY_BRACKETS_CLOSE.isEqualTo(nextSub = consume())) {
			consume(TokenConstants.COMMA);
			Token token = consumeToken();
			consume(TokenConstants.SEMICOLON);

			PreAttribute preAttribute = new PreAttribute(nextSub, getInt(token));
			container.addPreAttribute(preAttribute);
		}
	}

	/**
	 * A TokenConstants.VALUES_KNOWLEDGE has been encountered. We now add the textIDs of the processes to a preList of the container.
	 * @param container to add the textIDs to
	 * @throws Exception
	 */
	private void addKnowledge(CreatureContainer container) throws Exception {
		// knowledge { proOne; proTwo; }
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		Token nextToken;
		while (!TokenConstants.CURLY_BRACKETS_CLOSE.equals(nextToken = consumeToken())) {
			consume(TokenConstants.SEMICOLON);

			container.addPreKnownProcess(nextToken.getValue());
		}
	}

	// ###################################################################################
	// ################################ Tile #############################################
	// ###################################################################################

	private void createTile() throws Exception {
		// Tile : tTextId { ... }
		consume(TokenConstants.COLON);
		String textId = consume();
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		TileContainer protoTile = new TileContainer(textId);
		Data.addContainer(protoTile);

		while (true) {
			String next = consume();
			if (TokenConstants.VALUE_NAME.isEqualTo(next)) { // name definition
				consume(TokenConstants.ASSIGNMENT);

				String name = consume();
				protoTile.setName(name);

				consume(TokenConstants.SEMICOLON);

			} else if (TokenConstants.VALUE_PREFERREDHEIGHT.isEqualTo(next)) { // preferred height definition
				consume(TokenConstants.ASSIGNMENT);

				Token height = consumeToken();
				protoTile.setPreferredHeight(getInt(height));

				Token nextSub = consumeToken();
				if (TokenConstants.COMMA.equals(nextSub)) {
					Token blur = consumeToken();
					protoTile.setPreferredHeightBlur(getInt(blur));

					consume(TokenConstants.SEMICOLON);

				} else if (TokenConstants.SEMICOLON.equals(nextSub)) {
					// exit definition
				} else {
					Logger.error("Expected '" + TokenConstants.SEMICOLON.getValue() + "' but got '" + nextSub + "' on line " + nextSub.getLine());
				}

			} else if (TokenConstants.VALUE_PREFERREDHEIGHTBLUR.isEqualTo(next)) { // preferred height blur definition
				consume(TokenConstants.ASSIGNMENT);

				Token blur = consumeToken();
				protoTile.setPreferredHeightBlur(getInt(blur));

				consume(TokenConstants.SEMICOLON);

			} else if (TokenConstants.VALUE_COLOR.isEqualTo(next)) { // color definition
				consume(TokenConstants.CURLY_BRACKETS_OPEN);

				String seperator;
				Token redDeviation = null, greenDeviation = null, blueDeviation = null;

				Token red = consumeToken();
				seperator = consume();
				if (TokenConstants.COMMA.isEqualTo(seperator)) {
					redDeviation = consumeToken();
					consume(TokenConstants.SEMICOLON);
				}

				Token green = consumeToken();
				seperator = consume();
				if (TokenConstants.COMMA.isEqualTo(seperator)) {
					greenDeviation = consumeToken();
					consume(TokenConstants.SEMICOLON);
				}

				Token blue = consumeToken();
				seperator = consume();
				if (TokenConstants.COMMA.isEqualTo(seperator)) {
					blueDeviation = consumeToken();
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

			} else if (TokenConstants.VALUES_ATTRIBUTES.isEqualTo(next)) { // list of attributes
				addAttributes(protoTile);

			} else if (TokenConstants.CURLY_BRACKETS_CLOSE.isEqualTo(next)) { // end of definition
				return;

			}
		}
	}

	// ###################################################################################
	// ################################ Creature #########################################
	// ###################################################################################

	private void createCreature() throws Exception {
		// Creature : cTextId { ... }
		consume(TokenConstants.COLON);
		String textId = consume();
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		CreatureContainer protoCreature = new CreatureContainer(textId);
		Data.addContainer(protoCreature);

		while (true) {
			String next = consume();
			if (TokenConstants.VALUE_NAME.isEqualTo(next)) { // name definition
				consume(TokenConstants.ASSIGNMENT);

				String name = consume();
				protoCreature.setName(name);

				consume(TokenConstants.SEMICOLON);

			} else if (TokenConstants.VALUES_ATTRIBUTES.isEqualTo(next)) { // list of attributes
				addAttributes(protoCreature);

			} else if (TokenConstants.VALUES_KNOWLEDGE.isEqualTo(next)) { // list of known processes
				addKnowledge(protoCreature);

			} else if (TokenConstants.CURLY_BRACKETS_CLOSE.isEqualTo(next)) { // end of definition
				return;

			}
		}
	}

}
