package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.Script;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.BinaryOperationExecuter;
import engine.parser.tokenization.Token;

public class BinaryExpressionNode extends AbstractScriptNode {

	private Token operator;
	boolean bracketed = false;

	public BinaryExpressionNode(Token operator, AbstractScriptNode left, AbstractScriptNode right) {
		this.operator = operator;
		subNodes = new AbstractScriptNode[2];
		subNodes[0] = left;
		subNodes[1] = right;
	}

	@Override
	public Variable execute(Instance instance, Script script) throws ScriptInterruptedException {
		return BinaryOperationExecuter.executeOperation(instance, script, this);
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "Binary Expression " + operator + " (" + operator.getPrecedence() + ")");
		subNodes[0].print(indentation + " ");
		subNodes[1].print(indentation + " ");
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public Token getOperator() {
		return operator;
	}

	public AbstractScriptNode getLeft() {
		return subNodes[0];
	}
	public void setLeft(AbstractScriptNode left) {
		subNodes[0] = left;
	}

	public AbstractScriptNode getRight() {
		return subNodes[1];
	}
	public void setRight(AbstractScriptNode right) {
		subNodes[1] = right;
	}

	public boolean isBracketed() {
		return bracketed;
	}
	public void setBracketed(boolean bracketed) {
		this.bracketed = bracketed;
	}

}
