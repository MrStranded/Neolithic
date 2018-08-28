package parser;

import engine.parser.constants.TokenConstants;
import engine.parser.constants.TokenType;
import engine.parser.tokenization.Token;
import engine.parser.tokenization.Tokenizer;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TokenizerTest {

	@Test
	public void testTokenConstantsEqualities() {
		// equals(char)
		assertTrue(TokenConstants.SEMICOLON.equals(';'));
		assertTrue(!TokenConstants.SEMICOLON.equals('.'));

		// equals(codePoint1, codePoint2)
		assertTrue(TokenConstants.COMMENT_OPEN.equals('/', '*'));
		assertTrue(!TokenConstants.COMMENT_OPEN.equals('/', '.'));
		assertTrue(!TokenConstants.COMMENT_OPEN.equals('.', '*'));

		// equals(Token)
		assertTrue(TokenConstants.ATTRIBUTE.equals(new Token(TokenType.KEYWORD, "Attribute", 0)));
		assertTrue(TokenConstants.ATTRIBUTE.equals(new Token(TokenType.IDENTIFIER, "Attribute", 0))); // TokenType shall be ignored
		assertTrue(!TokenConstants.ATTRIBUTE.equals(new Token(TokenType.KEYWORD, "Bttribute", 0)));

		// equals(TokenType, Value)
		assertTrue(TokenConstants.ATTRIBUTE.equals(TokenType.KEYWORD, "Attribute"));
		assertTrue(!TokenConstants.ATTRIBUTE.equals(TokenType.IDENTIFIER, "Attribute")); // here we do not ignore the TokenType
		assertTrue(!TokenConstants.ATTRIBUTE.equals(TokenType.KEYWORD, "Attribatez"));
	}

	@Test
	public void testTokenizerNoComments() {
		List<Token> tokens;
		try {
			tokens = Tokenizer.tokenize(new FileReader("src/test/resources/tokenizerTestNoComments"));

			assertEquals(TokenType.IDENTIFIER, tokens.get(0).getType());
			assertEquals("abc", tokens.get(0).getValue());
			assertEquals(1, tokens.get(0).getLine());

			assertEquals(TokenType.SEPARATOR, tokens.get(1).getType());
			assertEquals(",", tokens.get(1).getValue());
			assertEquals(1, tokens.get(1).getLine());

			assertEquals(TokenType.OPERATOR, tokens.get(5).getType());
			assertEquals("+", tokens.get(5).getValue());
			assertEquals(1, tokens.get(5).getLine());

			assertEquals(TokenType.IDENTIFIER, tokens.get(10).getType());
			assertEquals("xD18", tokens.get(10).getValue());
			assertEquals(3, tokens.get(10).getLine());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testTokenizerComments() {
		List<Token> tokens;
		try {
			tokens = Tokenizer.tokenize(new FileReader("src/test/resources/tokenizerTestComments"));

			assertEquals(TokenType.IDENTIFIER, tokens.get(0).getType());
			assertEquals("abc", tokens.get(0).getValue());
			assertEquals(1, tokens.get(0).getLine());

			assertEquals(TokenType.OPERATOR, tokens.get(2).getType());
			assertEquals("-", tokens.get(2).getValue());
			assertEquals(1, tokens.get(2).getLine());

			assertEquals(TokenType.IDENTIFIER, tokens.get(4).getType());
			assertEquals("te", tokens.get(4).getValue());
			assertEquals(2, tokens.get(4).getLine());

			assertEquals(TokenType.IDENTIFIER, tokens.get(7).getType());
			assertEquals("br", tokens.get(7).getValue()); // br/*/ä - the comment cuts the token in half
			assertEquals(3, tokens.get(7).getLine());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testTokenizerStrings() {
		List<Token> tokens;
		try {
			tokens = Tokenizer.tokenize(new FileReader("src/test/resources/tokenizerTestStrings"));

			assertEquals(TokenType.LITERAL, tokens.get(1).getType());
			assertEquals(" this   is // a string ", tokens.get(1).getValue());
			assertEquals(1, tokens.get(1).getLine());

			assertEquals(TokenType.IDENTIFIER, tokens.get(2).getType());
			assertEquals("etc", tokens.get(2).getValue());
			assertEquals(1, tokens.get(2).getLine());

			assertEquals(TokenType.IDENTIFIER, tokens.get(4).getType());
			assertEquals("because", tokens.get(4).getValue());
			assertEquals(2, tokens.get(4).getLine());

			assertEquals(TokenType.LITERAL, tokens.get(8).getType());
			assertEquals("comments", tokens.get(8).getValue()); // br/*/ä - the comment cuts the token in half
			assertEquals(2, tokens.get(8).getLine());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
