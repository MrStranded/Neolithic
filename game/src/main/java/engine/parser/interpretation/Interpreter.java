package engine.parser.interpretation;

import engine.data.proto.Container;
import engine.data.proto.Data;
import engine.data.proto.ProtoAttribute;
import engine.data.variables.DataType;
import engine.graphics.renderer.color.RGBA;
import engine.parser.Logger;
import engine.parser.constants.TokenConstants;
import engine.parser.constants.TokenType;
import engine.parser.tokenization.Token;

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
			Logger.error("Wrong Token! Expected " + tokenConstant.getValue()
					+ " (" + Character.codePointAt(tokenConstant.getValue().toCharArray(), 0) + ") "
					+ " but found " + next.getValue()
					+ " (" + Character.codePointAt(next.getValue().toCharArray(), 0) + ") ");
			StringBuilder successive = new StringBuilder();
			int i = 0;
			while (tokenIterator.hasNext() && i < 20) {
				successive.append(tokenIterator.next().getValue());
				successive.append(" ");
				i++;
			}
			Logger.error("Right before: " + successive);
			throw new Exception("Wrong Token! Expected " + tokenConstant.getValue() + " but found " + next.getValue());

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
	// ################################ Tile #############################################
	// ###################################################################################

	private void createTile() throws Exception {
		// Tile : tTextId { ... }
		consume(TokenConstants.COLON);
		String textId = consume();
		consume(TokenConstants.CURLY_BRACKETS_OPEN);

		Container protoTile = new Container(textId, DataType.TILE);
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

					String height = consume();
					protoTile.setPreferredHeight(Integer.parseInt(height));

					consume(TokenConstants.SEMICOLON);

			} else if (TokenConstants.VALUE_COLOR.isEqualTo(next)) { // color definition
				consume(TokenConstants.CURLY_BRACKETS_OPEN);

				Token red = consumeToken();
				consume(TokenConstants.SEMICOLON);
				Token green = consumeToken();
				consume(TokenConstants.SEMICOLON);
				Token blue = consumeToken();
				consume(TokenConstants.SEMICOLON);

				double colorRed = Double.parseDouble(red.getValue()) / 255d;
				double colorGreen = Double.parseDouble(green.getValue()) / 255d;
				double colorBlue = Double.parseDouble(blue.getValue()) / 255d;

				protoTile.setColor(new RGBA(colorRed, colorGreen, colorBlue));

				consume(TokenConstants.CURLY_BRACKETS_CLOSE);

			} else if (TokenConstants.CURLY_BRACKETS_CLOSE.isEqualTo(next)) { // end of definition
				return;

			}
		}
	}

}
