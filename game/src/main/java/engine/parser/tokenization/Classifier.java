package engine.parser.tokenization;

import engine.parser.constants.CharacterClass;

public class Classifier {

	public static CharacterClass classify(int c) {
		if (Character.isDigit(c)) {
			return CharacterClass.NUMBER;
		}

		if (Character.isAlphabetic(c)) {
			return CharacterClass.LETTER;
		}

		switch (c) {
			case '>': case '<': case '=': case '!': case '&': case '|': case '+': case '-': case '*': case '/': case '^':
				return CharacterClass.OPERATOR;

			case ';': case ':': case ',': case '(': case ')': case '{': case '}': case '[': case ']':
				return CharacterClass.SEPARATOR;

			case '.':
				return CharacterClass.POINT;

			case ' ': case 9: // 9 being the tab
				return CharacterClass.WHITE_SPACE;

			case '_':
				return CharacterClass.LETTER; // we want to use under score for variable names

			case '"':
				return CharacterClass.QUOTATION_MARK;

			case '\\':
				return CharacterClass.BACK_SLASH;

			case 10: case 13:
				return CharacterClass.LINE_BREAK;

			default:
				return CharacterClass.UNDEFINED;

		}
	}

}
