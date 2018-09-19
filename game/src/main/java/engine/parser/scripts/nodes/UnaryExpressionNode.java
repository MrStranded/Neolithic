package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.Script;
import engine.data.variables.Variable;
import engine.parser.scripts.execution.UnaryOperationExecuter;
import engine.parser.tokenization.Token;

public class UnaryExpressionNode extends AbstractScriptNode {

	private Token operator;

	public UnaryExpressionNode(Token operator, AbstractScriptNode subNode) {
		this.operator = operator;
		subNodes = new AbstractScriptNode[1];
		subNodes[0] = subNode;
	}

	@Override
	public Variable execute(Instance instance, Script script) {
		return UnaryOperationExecuter.executeOperation(instance, script, this);
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "Unary Expression " + operator);
		subNodes[0].print(indentation + " ");
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public Token getOperator() {
		return operator;
	}

	public AbstractScriptNode getSubNode() {
		return subNodes[0];
	}
	public void setSubNode(AbstractScriptNode subNode) {
		subNodes[0] = subNode;
	}

}
