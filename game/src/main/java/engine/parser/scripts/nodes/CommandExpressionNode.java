package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.variables.Variable;
import engine.parser.tokenization.Token;

import java.util.List;

public class CommandExpressionNode extends AbstractScriptNode {

	private Token command;

	public CommandExpressionNode(Token command, List<AbstractScriptNode> parameters) {
		this.command = command;

		subNodes = new AbstractScriptNode[parameters.size()];
		int i=0;
		for (AbstractScriptNode node : parameters) {
			subNodes[i++] = node;
		}
	}

	@Override
	public Variable execute(Instance instance) {
		return null;
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "Command: " + command);
		for (AbstractScriptNode node : subNodes) {
			node.print(indentation + " ");
		}
	}
}
