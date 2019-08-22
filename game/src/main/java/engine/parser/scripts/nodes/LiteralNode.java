package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.scripts.Script;
import engine.data.variables.Variable;
import engine.parser.tokenization.Token;
import engine.parser.utils.TokenNumerifier;

public class LiteralNode extends AbstractScriptNode {

	private Token literal;

	public LiteralNode(Token literal) {
		this.literal = literal;
	}

	@Override
	public Variable execute(Instance instance, Script script) {
		if (TokenNumerifier.isNumber(literal, true)) {
			return new Variable(TokenNumerifier.getDouble(literal)); // double
		} else {
			return new Variable(literal.getValue()); // string
		}
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "Literal: " + literal);
	}
}
