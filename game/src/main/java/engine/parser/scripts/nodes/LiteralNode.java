package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.variables.Variable;
import engine.parser.tokenization.Token;

public class LiteralNode extends AbstractScriptNode {

	private Token literal;

	public LiteralNode(Token literal) {
		this.literal = literal;
	}

	@Override
	public Variable execute(Instance instance) {
		return null;
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "Literal: " + literal);
	}
}
