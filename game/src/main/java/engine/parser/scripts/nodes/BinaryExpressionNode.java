package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.structures.trees.binary.BinaryTree;
import engine.data.variables.Variable;
import engine.parser.tokenization.Token;

public class BinaryExpressionNode extends AbstractScriptNode {

	private Token operator;

	public BinaryExpressionNode(Token operator, AbstractScriptNode left, AbstractScriptNode right) {
		this.operator = operator;
		subNodes = new AbstractScriptNode[2];
		subNodes[0] = left;
		subNodes[1] = right;
	}

	@Override
	public Variable execute(Instance instance) {
		return null;
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "Binary Expression " + operator);
		subNodes[0].print(indentation + " ");
		subNodes[1].print(indentation + " ");
	}
}
