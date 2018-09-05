package engine.parser.scripts.execution;

import engine.data.entities.Instance;
import engine.data.variables.Variable;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.nodes.BinaryExpressionNode;
import engine.parser.scripts.nodes.IdentifierNode;
import engine.parser.scripts.nodes.ScriptCallNode;
import engine.parser.tokenization.Token;

public class OperationExecuter {

	public static Variable executeOperation(Instance self, BinaryExpressionNode binaryNode) {
		System.out.println("execute binary operation " + binaryNode.getOperator());

		Token operator = binaryNode.getOperator();
		Variable left = binaryNode.getLeft().execute(self);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& =
		if (TokenConstants.ASSIGNMENT.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self);

			left.copyValue(right);
			if (self != null && left.hasName()) {
				self.addVariable(left);
			}
			return left;

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& ->
		} else if (TokenConstants.OBJECT_OPERATOR.equals(operator)) {
			if (binaryNode.getRight().getClass() == ScriptCallNode.class) { // script call
				((ScriptCallNode) binaryNode.getRight()).setTarget(left);
			} else if (binaryNode.getRight().getClass() == IdentifierNode.class) { // variable
				((IdentifierNode) binaryNode.getRight()).setTarget(left);
			}

			return binaryNode.getRight().execute(self);

			// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& -
		} else if (TokenConstants.MINUS.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self);

			return new Variable(left.getDouble() - right.getDouble());

		}

		return new Variable();
	}

}
