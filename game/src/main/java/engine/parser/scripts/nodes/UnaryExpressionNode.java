package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.scripts.Script;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.UnaryOperationExecuter;
import engine.parser.tokenization.Token;
import engine.parser.utils.Logger;

public class UnaryExpressionNode extends AbstractScriptNode {

	private Token operator;

	public UnaryExpressionNode(Token operator, AbstractScriptNode subNode) {
		this.operator = operator;
		subNodes = new AbstractScriptNode[1];
		subNodes[0] = subNode;
	}

	@Override
	public Variable execute(Instance instance, Script script) throws ScriptInterruptedException {
		return UnaryOperationExecuter.executeOperation(instance, script, this);
	}

	@Override
	public void print(String indentation) {
		Logger.raw(indentation + "Unary Expression " + operator + " (" + operator.getPrecedence() + ")");
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
