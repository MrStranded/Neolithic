package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.variables.Variable;

import java.util.List;

public class MultiStatementNode extends AbstractScriptNode {

	public MultiStatementNode(List<AbstractScriptNode> nodeList) {
		subNodes = new AbstractScriptNode[nodeList.size()];
		int i=0;
		for (AbstractScriptNode node : nodeList) {
			subNodes[i++] = node;
		}
	}

	@Override
	public Variable execute(Instance instance) {
		for (AbstractScriptNode node : subNodes) {
			node.execute(instance);
		}
		return null;
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "MultiStatement");
		for (AbstractScriptNode node : subNodes) {
			node.print(indentation + ">");
		}
	}
}
