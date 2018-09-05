package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.variables.Variable;
import engine.parser.tokenization.Token;

import java.util.List;

public class ScriptCallNode extends AbstractScriptNode {

	private Token identifier;

	public ScriptCallNode(Token identifier, List<AbstractScriptNode> parameters) {
		this.identifier = identifier;

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
		System.out.println(indentation + "ScriptCall: " + identifier);
		for (AbstractScriptNode node : subNodes) {
			node.print(indentation + " ");
		}
	}
}
