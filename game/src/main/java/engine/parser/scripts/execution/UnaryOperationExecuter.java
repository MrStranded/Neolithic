package engine.parser.scripts.execution;

import engine.data.entities.Instance;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.nodes.IdentifierNode;
import engine.parser.scripts.nodes.ScriptCallNode;
import engine.parser.scripts.nodes.UnaryExpressionNode;
import engine.parser.tokenization.Token;

public class UnaryOperationExecuter {

	public static Variable executeOperation(Instance self, UnaryExpressionNode unaryNode) {
		Token operator = unaryNode.getOperator();
		Variable variable = unaryNode.getSubNode().execute(self);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& ++
		if (TokenConstants.SINGLE_INCREMENT.equals(operator)) {

			if (variable.getType() == DataType.NUMBER) {
				variable.setDouble(variable.getDouble() + 1);
			}
			return variable;

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& --
		} else if (TokenConstants.SINGLE_DECREMENT.equals(operator)) {

			if (variable.getType() == DataType.NUMBER) {
				variable.setDouble(variable.getDouble() - 1);
			}
			return variable;

		}

		return new Variable();
	}

}
