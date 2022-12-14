package engine.parser.utils;

import engine.parser.constants.TokenType;
import engine.parser.tokenization.Token;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenNumerifierTest {

    @Test
    void isNumber_specialCases() {
        assertFalse(TokenNumerifier.isNumber(null, false));
        assertFalse(TokenNumerifier.isNumber(new Token(TokenType.KEYWORD, "123", 0), false));
    }

    @Test
    void isNumber_integer() {
        assertTrue(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "123", 0), false));
        assertTrue(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "0", 0), false));
        assertTrue(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "+100", 0), false));
        assertTrue(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "-12", 0), false));

        assertFalse(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "+", 0), false));
        assertFalse(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "-", 0), false));
        assertFalse(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "123.69", 0), false));
        assertFalse(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "abc", 0), false));
        assertFalse(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "abc123", 0), false));
        assertFalse(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "123abc", 0), false));
    }

    @Test
    void isNumber_double() {
        assertTrue(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "123", 0), true));
        assertTrue(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "0", 0), true));
        assertTrue(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "+100", 0), true));
        assertTrue(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "-12", 0), true));
        assertTrue(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "123.69", 0), true));
        assertTrue(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "+100.0011", 0), true));
        assertTrue(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "-12.420", 0), true));

        assertFalse(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "+", 0), true));
        assertFalse(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "-", 0), true));
        assertFalse(TokenNumerifier.isNumber(new Token(TokenType.LITERAL, "abc", 0), true));
    }

}