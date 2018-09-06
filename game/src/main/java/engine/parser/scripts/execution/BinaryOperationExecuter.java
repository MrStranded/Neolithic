package engine.parser.scripts.execution;

import engine.data.entities.Instance;
import engine.data.structures.Script;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.nodes.BinaryExpressionNode;
import engine.parser.scripts.nodes.IdentifierNode;
import engine.parser.scripts.nodes.ScriptCallNode;
import engine.parser.tokenization.Token;

public class BinaryOperationExecuter {

	public static Variable executeOperation(Instance self, Script script, BinaryExpressionNode binaryNode) {
		Token operator = binaryNode.getOperator();
		Variable left = binaryNode.getLeft().execute(self, script);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& =
		if (TokenConstants.ASSIGNMENT.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			left.copyValue(right);
			if (left.hasName()) { // only if variable has a name!
				if (binaryNode.getLeft().getClass() == IdentifierNode.class) {
					Instance targetInstance = ((IdentifierNode) binaryNode.getLeft()).getTargetInstance();
					if (targetInstance != null) {
						targetInstance.addVariable(left);
					} else {
						script.addVariable(left);
					}
				} else {
					script.addVariable(left);
				}
			}
			return left;

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& ->
		} else if (TokenConstants.OBJECT_OPERATOR.equals(operator)) {
			if (binaryNode.getRight().getClass() == ScriptCallNode.class) { // script call
				((ScriptCallNode) binaryNode.getRight()).setTarget(left);
			} else if (binaryNode.getRight().getClass() == IdentifierNode.class) { // variable
				((IdentifierNode) binaryNode.getRight()).setTarget(left);
			}

			return binaryNode.getRight().execute(self, script);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& +
		} else if (TokenConstants.PLUS.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			if (left.getType() == DataType.NUMBER && right.getType() == DataType.NUMBER) { // normal addition
				return new Variable(left.getDouble() + right.getDouble());
			} else { // string concatenation
				return new Variable(left.getString() + right.getString());
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& -
		} else if (TokenConstants.MINUS.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			return new Variable(left.getDouble() - right.getDouble());

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& >
		} else if (TokenConstants.GREATER.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			return new Variable(left.getDouble() > right.getDouble() ? 1 : 0);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& <
		} else if (TokenConstants.LESSER.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			return new Variable(left.getDouble() < right.getDouble() ? 1 : 0);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& &&
		} else if (TokenConstants.AND.equals(operator)) {
			if (left.isNull()) {
				return new Variable(0);
			}

			Variable right = binaryNode.getRight().execute(self, script);

			return new Variable(right.isNull() ? 0 : 1);

		}

		return new Variable();
	}

}
