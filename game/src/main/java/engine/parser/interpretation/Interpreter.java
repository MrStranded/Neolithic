package engine.parser.interpretation;

import engine.parser.constants.TokenConstants;
import engine.parser.tokenization.Token;

import java.util.Iterator;
import java.util.List;

public class Interpreter {

	private Iterator<Token> tokenIterator;

	public Interpreter(List<Token> tokens) {
		tokenIterator = tokens.iterator();
	}

	public void interpret() throws Exception {
		// outer most level of a script
		while (tokenIterator.hasNext()) {
			Token next = tokenIterator.next();

			if (TokenConstants.ATTRIBUTE.equals(next)) {        // Attribute
				System.out.println("an attribute!");

				createAttribute();
			} else if (TokenConstants.TILE.equals(next)) {      // Tile
				System.out.println("tile");
			}
		}
	}

	private String consume(TokenConstants tokenConstant) throws Exception {
		Token next = tokenIterator.next();
		if (tokenConstant.equals(next)) {
			return next.getValue();
		} else {
			throw new Exception("Wrong Token! Expected " + tokenConstant.getValue() + " but found " + next.getValue());
		}
	}

	private String consume() {
		Token next = tokenIterator.next();
		return next.getValue();
	}

	private void createAttribute() throws Exception {
		// Attribute : attTextId { ... }
		consume(TokenConstants.COLON);
		String textId = consume();
	}

}
