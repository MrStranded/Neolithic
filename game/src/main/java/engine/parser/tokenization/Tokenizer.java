package engine.parser.tokenization;

import engine.parser.utils.Logger;
import engine.parser.constants.CharacterClass;
import engine.parser.constants.TokenConstants;
import engine.parser.constants.TokenType;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

	private static int textLine;

	public static List<Token> tokenize(FileReader fileReader) {
		CharacterClass currentClass = CharacterClass.UNDEFINED;

		int c;
		int previousChar = 0;
		StringBuilder token = new StringBuilder();

		textLine = 1;

		boolean inString = false;
		boolean inComment = false;
		boolean isBlockComment = false;

		List<Token> tokens = new ArrayList<>(64);

		try {
			while ((c = fileReader.read()) != -1) {
				CharacterClass classification = Classifier.classify(c);

				switch (classification) {
					case QUOTATION_MARK:
						if (!inComment) {
							if (previousChar == '\\') { // \" quotation mark with preceding back slash is added
								addChar(token, c);
							} else { // we switch the appending mode
								if (inString) { // end of string literal
									addToken(tokens, CharacterClass.QUOTATION_MARK, token);
									currentClass = CharacterClass.QUOTATION_MARK;
								} else { // beginning of string literal
									addToken(tokens, currentClass, token);
								}
								inString = !inString;
							}
						}
						break;

					case BACK_SLASH: // back slashes are only added, when the previous char was also a back slash
						if (!inComment) {
							if (previousChar == '\\') {
								addChar(token, c);
								c = 0; // this is used to set the next previousChar to zero (directly doing this would be overwritten at the end of the loop)
								// this is so that \\\" would not be interpreted as two back slashes but only as one and a quotation mark
							}
						}
						break;

					case WHITE_SPACE:
						if (!inComment) {
							if (inString) {
								addChar(token, c);
							} else {
								if (currentClass != CharacterClass.WHITE_SPACE) {
									addToken(tokens, currentClass, token);
									currentClass = CharacterClass.WHITE_SPACE;
								}
							}
						}
						break;

					case LINE_BREAK:
						if (inComment && !isBlockComment) {
							inComment = false;
						}
						if (c == 10) { // common denominator for windows / unix
							textLine++;
						}
						break;

					case OPERATOR:
						int length = token.length();

						if (!inString) {
							if (TokenConstants.COMMENT.equals(previousChar, c)) { // line comment
								if (!inComment) {
									inComment = true;
									isBlockComment = false;
									token.delete(Math.max(length - 2, 0), length);
								}
							} else if (TokenConstants.COMMENT_OPEN.equals(previousChar, c)) { // block comment open
								if (!inComment) {
									inComment = true;
									isBlockComment = true;
									token.delete(Math.max(length - 2, 0), length);
								}
							} else if (TokenConstants.COMMENT_CLOSE.equals(previousChar, c)) { // block comment close
								if (inComment && isBlockComment) {
									inComment = false;
								}
							} else {
								if (!inComment) {
									if (currentClass != CharacterClass.OPERATOR) { // new character class -> new token
										addToken(tokens, currentClass, token);
										currentClass = CharacterClass.OPERATOR;
									}
									addChar(token, c);
								}
							}
						} else { // we're in a string -> we do not care about comments
							addChar(token, c);
						}
						break;

					case SEPARATOR:
						if (!inComment) {
							if (!inString) {
								// separators always create a new token, multiple subsequent separators are NOT added together
								addToken(tokens, currentClass, token);
								currentClass = CharacterClass.SEPARATOR;
							}
							addChar(token, c);
						}
						break;

					case NUMBER:
						if (!inComment) {
							if (!inString) {
								if ((currentClass != CharacterClass.LETTER) && (currentClass != CharacterClass.NUMBER) && (currentClass != CharacterClass.DECIMALNUMBER)) {
									addToken(tokens, currentClass, token);
									currentClass = CharacterClass.NUMBER;
								}
							}
							addChar(token, c);
						}
						break;

					case POINT:
						if (!inComment) {
							if (!inString) {
								if (currentClass == CharacterClass.POINT) { // double point possibly
									currentClass = CharacterClass.OPERATOR;
								} else if (currentClass != CharacterClass.NUMBER) { // the point behaves like an operator
									addToken(tokens, currentClass, token);
									currentClass = CharacterClass.OPERATOR;
								} else { // the point designates the decimal point of a number
									currentClass = CharacterClass.DECIMALNUMBER;
								}
							}
							addChar(token, c);
						}
						break;

					case LETTER: case DECIMALNUMBER: case UNDEFINED: default:
						if (!inComment) {
							if (!inString) {
								if (classification != currentClass) {
									addToken(tokens, currentClass, token);
									currentClass = classification;
								}
							}
							addChar(token, c);
						}
						break;

				}

				previousChar = c;
			}

			if (token.length() > 0) { // last token (never forgetti)
				addToken(tokens, currentClass, token);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (inString) { // unclosed String!
			Logger.error("Unclosed String!");
		}

		return tokens;
	}

	private static void addChar(StringBuilder token, int c) {
		token.append(Character.toChars(c));
	}

	private static void addToken(List<Token> list, CharacterClass characterClass, StringBuilder token) {
		if (token.length() > 0) {
			Token result = null;

			if (characterClass == CharacterClass.QUOTATION_MARK || characterClass == CharacterClass.NUMBER || characterClass == CharacterClass.DECIMALNUMBER) {
				result = new Token(TokenType.LITERAL, token.toString(), textLine);
			} else {
				for (TokenConstants t : TokenConstants.values()) {
					if (t.getValue().equals(token.toString())) {
						result = new Token(t.getType(), t.getValue(), textLine);
						break;
					}
				}
				if (result == null) {
					result = new Token(TokenType.IDENTIFIER, token.toString(), textLine);
				}
			}

			list.add(result);
			token.delete(0, token.length());
		}
	}

}
