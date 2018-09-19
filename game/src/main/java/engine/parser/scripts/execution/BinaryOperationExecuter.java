package engine.parser.scripts.execution;

import engine.data.entities.Instance;
import engine.data.Script;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.nodes.BinaryExpressionNode;
import engine.parser.scripts.nodes.IdentifierNode;
import engine.parser.scripts.nodes.ScriptCallNode;
import engine.parser.tokenization.Token;
import engine.parser.utils.Logger;

public class BinaryOperationExecuter {

	public static Variable executeOperation(Instance self, Script script, BinaryExpressionNode binaryNode) {
		Token operator = binaryNode.getOperator();
		Variable left = binaryNode.getLeft().execute(self, script);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& =
		if (TokenConstants.ASSIGNMENT.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			left.copyValue(right);
			return left;

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& ->
		} else if (TokenConstants.OBJECT_OPERATOR.equals(operator)) {
			if (binaryNode.getRight().getClass() == ScriptCallNode.class) { // script call
				((ScriptCallNode) binaryNode.getRight()).setTarget(left);
			} else if (binaryNode.getRight().getClass() == IdentifierNode.class) { // variable
				((IdentifierNode) binaryNode.getRight()).setTarget(left);
			}
			return binaryNode.getRight().execute(self, script);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& .
		} else if (TokenConstants.POINT.equals(operator)) {
			IdentifierNode subNode = (IdentifierNode) binaryNode.getRight();
			subNode.setTarget(left);
			subNode.markAsAttributeIdentifier();

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

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& *
		} else if (TokenConstants.TIMES.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			return new Variable(left.getDouble() * right.getDouble());

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& /
		} else if (TokenConstants.DIVIDE.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			if (right.isNull()) {
				Logger.error("Cannot divide by zero! Line: " + operator.getLine());
				return new Variable();
			}

			return new Variable(left.getDouble() / right.getDouble());

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& %
		} else if (TokenConstants.MODULO.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			return new Variable(left.getDouble() % right.getDouble());

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& ^
		} else if (TokenConstants.POWER.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			return new Variable(Math.pow(left.getDouble(), right.getDouble()));

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& +=
		} else if (TokenConstants.QUICK_PLUS.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			if (left.getType() == DataType.NUMBER && right.getType() == DataType.NUMBER) { // normal addition
				left.setDouble(left.getDouble() + right.getDouble());
				return left;
			} else { // string concatenation
				left.setString(left.getString() + right.getString());
				return left;
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& ==
		} else if (TokenConstants.EQUAL.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			return new Variable(left.equals(right) ? 1 : 0);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& !=
		} else if (TokenConstants.UNEQUAL.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			return new Variable(left.equals(right) ? 0 : 1);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& >
		} else if (TokenConstants.GREATER.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			return new Variable(left.getDouble() > right.getDouble() ? 1 : 0);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& <
		} else if (TokenConstants.LESSER.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			return new Variable(left.getDouble() < right.getDouble() ? 1 : 0);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& <=
		} else if (TokenConstants.LESSER_EQUAL.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			return new Variable(left.getDouble() <= right.getDouble() ? 1 : 0);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& >=
		} else if (TokenConstants.GREATER_EQUAL.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			return new Variable(left.getDouble() >= right.getDouble() ? 1 : 0);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& &&
		} else if (TokenConstants.AND.equals(operator)) {
			if (left.isNull()) { return new Variable(0); }

			Variable right = binaryNode.getRight().execute(self, script);

			return new Variable(right.isNull() ? 0 : 1);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& ||
		} else if (TokenConstants.OR.equals(operator)) {
			if (!left.isNull()) { return new Variable(1); }

			Variable right = binaryNode.getRight().execute(self, script);

			return new Variable(!right.isNull() ? 1 : 0);

		}

		return new Variable();
	}

}
