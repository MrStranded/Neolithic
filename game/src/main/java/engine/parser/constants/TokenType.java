package engine.parser.constants;

public enum TokenType {

	IDENTIFIER,
	KEYWORD,
	COMMAND,
	SEPARATOR,
	OPERATOR,
	LITERAL,
	COMMENT, // only used in tokenizer to specifiy comment starts and ends
	PROPERTY_TYPE,

}
