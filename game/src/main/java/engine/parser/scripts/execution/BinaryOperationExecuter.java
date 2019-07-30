package engine.parser.scripts.execution;

import engine.data.Data;
import engine.data.attributes.Attribute;
import engine.data.entities.Instance;
import engine.data.Script;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.nodes.BinaryExpressionNode;
import engine.parser.scripts.nodes.IdentifierNode;
import engine.parser.scripts.nodes.ScriptCallNode;
import engine.parser.tokenization.Token;
import engine.parser.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class BinaryOperationExecuter {

	public static Variable executeOperation(Instance self, Script script, BinaryExpressionNode binaryNode) throws ScriptInterruptedException {
		Token operator = binaryNode.getOperator();
		Variable left = binaryNode.getLeft().execute(self, script);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& =
		if (TokenConstants.ASSIGNMENT.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			if (left.getType() == DataType.ATTRIBUTE) { // attribute assignment
				return left.quickSetAttributeValue(right.getDouble());
			}

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
			if (binaryNode.getRight().getClass() == IdentifierNode.class) {
				IdentifierNode subNode = (IdentifierNode) binaryNode.getRight();
				subNode.setTarget(left);
				subNode.markAsAttributeIdentifier();
			} else {
				Logger.error("Wrong argument type after point operator: '" + binaryNode.getRight().getClass() + "' on line " + operator.getLine());
				Logger.error("Argument:");
				binaryNode.getRight().print("   ");
			}

			return binaryNode.getRight().execute(self, script);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& ..
		} else if (TokenConstants.DOUBLE_POINT.equals(operator)) {
			if (binaryNode.getRight().getClass() == IdentifierNode.class) {
				IdentifierNode subNode = (IdentifierNode) binaryNode.getRight();
				subNode.setTarget(left);
				subNode.markAsAttributeIdentifier();
				subNode.setRetrieveCompleteAttributeValue(true);
			} else {
				Logger.error("Wrong argument type after double point operator: '" + binaryNode.getRight().getClass() + "' on line " + operator.getLine());
				Logger.error("Argument:");
				binaryNode.getRight().print("   ");
			}

			return binaryNode.getRight().execute(self, script);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& +
		} else if (TokenConstants.PLUS.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			if ((left.getType() == DataType.NUMBER || left.getType() == DataType.ATTRIBUTE)
			&& (right.getType() == DataType.NUMBER || right.getType() == DataType.ATTRIBUTE)) { // normal addition
				return new Variable(left.getDouble() + right.getDouble());
			} else if (left.getType() == DataType.LIST) { // list concatenation
				if (right.getType() == DataType.LIST) { // merge two lists
					List<Variable> leftList = left.getList();
					List<Variable> rightList = right.getList();
					List<Variable> resultList = new ArrayList<>(leftList.size() + rightList.size());
					resultList.addAll(leftList);
					resultList.addAll(rightList);
					return new Variable(resultList);
				} else { // add element to list
					List<Variable> leftList = left.getList();
					List<Variable> resultList = new ArrayList<>(leftList.size() + 1);
					resultList.addAll(leftList);
					resultList.add(right);
					return new Variable(resultList);
				}
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

			if (right.isNull()) {
				Logger.error("Cannot calculate modulo with zero! Line: " + operator.getLine());
				return new Variable();
			}

			return new Variable(left.getDouble() % right.getDouble());

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& ^
		} else if (TokenConstants.POWER.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			return new Variable(Math.pow(left.getDouble(), right.getDouble()));

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& +=
		} else if (TokenConstants.QUICK_PLUS.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			if (left.getType() == DataType.NUMBER) { // normal addition
				left.setDouble(left.getDouble() + right.getDouble());
				return left;
			} else if (left.getType() == DataType.ATTRIBUTE) { // attribute addition
				return left.quickSetAttributeValue(left.getDouble() + right.getDouble());
			} else { // string concatenation
				left.setString(left.getString() + right.getString());
				return left;
			}

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& -=
		} else if (TokenConstants.QUICK_MINUS.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			if (left.getType() == DataType.ATTRIBUTE) { // attribute subtraction
				return left.quickSetAttributeValue(left.getDouble() - right.getDouble());
			}

			left.setDouble(left.getDouble() - right.getDouble());
			return left;

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& *=
		} else if (TokenConstants.QUICK_TIMES.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			if (left.getType() == DataType.ATTRIBUTE) { // attribute multiplication
				return left.quickSetAttributeValue(left.getDouble() * right.getDouble());
			}

			left.setDouble(left.getDouble() * right.getDouble());
			return left;

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& /=
		} else if (TokenConstants.QUICK_DIVIDE.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			if (right.isNull()) {
				Logger.error("Cannot divide by zero! Line: " + operator.getLine());
				return new Variable();
			}

			if (left.getType() == DataType.ATTRIBUTE) { // attribute division
				return left.quickSetAttributeValue(left.getDouble() / right.getDouble());
			}

			left.setDouble(left.getDouble() / right.getDouble());
			return left;

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& %=
		} else if (TokenConstants.QUICK_MODULO.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			if (right.isNull()) {
				Logger.error("Cannot calculate modulo with zero! Line: " + operator.getLine());
				return new Variable();
			}

			if (left.getType() == DataType.ATTRIBUTE) { // attribute modulo
				return left.quickSetAttributeValue(left.getDouble() % right.getDouble());
			}

			left.setDouble(left.getDouble() % right.getDouble());
			return left;

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& ^=
		} else if (TokenConstants.QUICK_POWER.equals(operator)) {
			Variable right = binaryNode.getRight().execute(self, script);

			if (left.getType() == DataType.ATTRIBUTE) { // attribute power
				return left.quickSetAttributeValue(Math.pow(left.getDouble(), right.getDouble()));
			}

			left.setDouble(Math.pow(left.getDouble(), right.getDouble()));
			return left;

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

        // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& >>
        } else if (TokenConstants.SHIFT_RIGHT.equals(operator)) {
            Variable right = binaryNode.getRight().execute(self, script);

            return new Variable(left.getInt() >> right.getInt());

        // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& <<
        } else if (TokenConstants.SHIFT_LEFT.equals(operator)) {
            Variable right = binaryNode.getRight().execute(self, script);

            return new Variable(left.getInt() << right.getInt());

        // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& &
        } else if (TokenConstants.BITWISE_AND.equals(operator)) {
            Variable right = binaryNode.getRight().execute(self, script);

            return new Variable(left.getInt() & right.getInt());

        // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& |
        } else if (TokenConstants.BITWISE_OR.equals(operator)) {
            Variable right = binaryNode.getRight().execute(self, script);

            return new Variable(left.getInt() | right.getInt());

        // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& ^^
        } else if (TokenConstants.BITWISE_XOR.equals(operator)) {
            Variable right = binaryNode.getRight().execute(self, script);

            return new Variable(left.getInt() ^ right.getInt());

		}

		return new Variable();
	}

}
