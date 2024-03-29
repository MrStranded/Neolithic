package engine.parser.utils;

import engine.parser.constants.TokenConstants;
import engine.parser.constants.TokenType;
import engine.parser.tokenization.Token;

public class TokenNumerifier {

	/**
	 * Checks whether the given token is a double value (isDouble == true) or an integer value (isDouble == false).
	 * @param token to check
	 * @param isDouble allow one decimal point or not
	 * @return true if the value is legit
	 */
	public static boolean isNumber(Token token, boolean isDouble) {
		if (token == null) {
			return false; // exit without error
		}

		if (token.getType() == TokenType.LITERAL) {
			boolean isNumber = false;
			boolean hadPoint = false;
			boolean firstChar = true;
			for (char c : token.getValue().toCharArray()) {
				if (!Character.isDigit(c)) {
					if (TokenConstants.POINT.equals(c) && !hadPoint && isDouble) {
						hadPoint = true;
					} else if (TokenConstants.PLUS.equals(c) && firstChar) {
						// nothing happens, we're happy, first char may be a plus
					} else if (TokenConstants.MINUS.equals(c) && firstChar) {
						// nothing happens, we're happy, first char may be a minus
					} else {
						isNumber = false;
						break;
					}
				} else {
					isNumber = true;
				}
				firstChar = false;
			}

			return isNumber;
		}
		return false;
	}

	/**
	 * Checks whether the given token is a double value (isDouble == true) or an integer value (isDouble == false).
	 * If not, an error report is issued.
	 * @param token to check
	 * @param isDouble allow one decimal point or not
	 * @return true if the value is legit
	 */
	public static boolean requireNumber(Token token, boolean isDouble) {
		if (token == null) {
			return false;
		}

		boolean number = isNumber(token, isDouble);

		if (!number) {
			if (isDouble) {
				Logger.error("Expected floating point number value but got '" + token.getValue() + "' on line " + token.getLine());
			} else {
				Logger.error("Expected integer number value but got '" + token.getValue() + "' on line " + token.getLine());
			}
		}

		return number;
	}

	/**
	 * Returns the token's value as an integer if possible.
	 * Otherwise it issues an error log and returns 0.
	 * @param token to convert
	 * @return the token's value or zero
	 */
	public static int getInt(Token token) {
		return requireNumber(token, false)? Integer.parseInt(token.getValue()) : 0;
	}

	/**
	 * Returns the token's value as an double if possible.
	 * Otherwise it issues an error log and returns 0.
	 * @param token to convert
	 * @return the token's value or zero
	 */
	public static double getDouble(Token token) {
		return requireNumber(token, true)? Double.parseDouble(token.getValue()) : 0;
	}

}
