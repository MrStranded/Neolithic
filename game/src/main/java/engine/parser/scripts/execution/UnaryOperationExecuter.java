package engine.parser.scripts.execution;

import engine.data.entities.Instance;
import engine.data.Script;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.nodes.IdentifierNode;
import engine.parser.scripts.nodes.ScriptCallNode;
import engine.parser.scripts.nodes.UnaryExpressionNode;
import engine.parser.tokenization.Token;

public class UnaryOperationExecuter {

	public static Variable executeOperation(Instance self, Script script, UnaryExpressionNode unaryNode) {
		Token operator = unaryNode.getOperator();

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& ++
		if (TokenConstants.SINGLE_INCREMENT.equals(operator)) {
			Variable variable = unaryNode.getSubNode().execute(self, script);

			if (variable.getType() == DataType.NUMBER) {
				variable.setDouble(variable.getDouble() + 1);
			}
			return variable;

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& --
		} else if (TokenConstants.SINGLE_DECREMENT.equals(operator)) {
			Variable variable = unaryNode.getSubNode().execute(self, script);

			if (variable.getType() == DataType.NUMBER) {
				variable.setDouble(variable.getDouble() - 1);
			}
			return variable;

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& ->
		} else if (TokenConstants.OBJECT_OPERATOR.equals(operator)) {
			if (unaryNode.getSubNode().getClass() == ScriptCallNode.class) { // script call
				((ScriptCallNode) unaryNode.getSubNode()).setTarget(new Variable(self));
			} else if (unaryNode.getSubNode().getClass() == IdentifierNode.class) { // variable
				((IdentifierNode) unaryNode.getSubNode()).setTarget(new Variable(self));
			}
			return unaryNode.getSubNode().execute(self, script);

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& !
		} else if (TokenConstants.NOT.equals(operator)) {
			Variable variable = unaryNode.getSubNode().execute(self, script);

			return new Variable(variable.isNull() ? 1 : 0);

		}

		return new Variable();
	}

}
