package parser;

import engine.data.proto.Data;
import engine.parser.constants.TokenType;
import engine.parser.interpretation.Interpreter;
import engine.parser.tokenization.Token;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class InterpreterTest {

	@Test
	public void testAttributeCreation() {
		Data.clear();

		List<Token> tokens = new ArrayList<>(9);
		tokens.add(new Token(TokenType.KEYWORD, "Attribute", 0));
		tokens.add(new Token(TokenType.SEPARATOR, ":", 0));
		tokens.add(new Token(TokenType.IDENTIFIER, "testAttribute", 0));
		tokens.add(new Token(TokenType.SEPARATOR, "{", 0));
		tokens.add(new Token(TokenType.KEYWORD, "name", 0));
		tokens.add(new Token(TokenType.OPERATOR, "=", 0));
		tokens.add(new Token(TokenType.LITERAL, "The Test Attribute", 0));
		tokens.add(new Token(TokenType.SEPARATOR, ";", 0));
		tokens.add(new Token(TokenType.SEPARATOR, "}", 0));

		try {
			new Interpreter(tokens, "").interpret();
		} catch (Exception e) {
			e.printStackTrace();
			fail("This should not produce an error.");
		}

		assertEquals(0, Data.getProtoAttributeID("testAttribute"));
		assertEquals("The Test Attribute", Data.getProtoAttribute(0).getName());
	}

	@Test
	public void testAttributeCreationError() {
		Data.clear();

		List<Token> tokens = new ArrayList<>(2);
		tokens.add(new Token(TokenType.KEYWORD, "Attribute", 0));
		tokens.add(new Token(TokenType.SEPARATOR, "(", 0));

		try {
			new Interpreter(tokens, "").interpret();

			fail("This should produce an error.");
		} catch (Exception e) {
			// an error is produced
		}
	}
}
