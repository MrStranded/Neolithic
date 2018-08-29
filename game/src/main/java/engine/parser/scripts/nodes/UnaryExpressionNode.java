package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.variables.Variable;
import engine.parser.tokenization.Token;

public class UnaryExpressionNode extends AbstractScriptNode {

	private Token operator;

	public UnaryExpressionNode(Token operator, AbstractScriptNode subNode) {
		subNodes = new AbstractScriptNode[1];
		subNodes[0] = subNode;
	}

	@Override
	public Variable execute(Instance instance) {
		return null;
	}

	@Override
	public void print() {
		System.out.println("Unary Expression " + operator);
		subNodes[0].print();
	}
}
