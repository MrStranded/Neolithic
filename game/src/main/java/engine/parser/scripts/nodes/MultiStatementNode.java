package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.structures.Script;
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
	public Variable execute(Instance instance, Script script) {
		boolean allTrue = true;
		for (AbstractScriptNode node : subNodes) {
			allTrue = !node.execute(instance, script).isNull() && allTrue; // allTrue is only true if all subNodes return Variables that are not null
		}
		return new Variable(allTrue ? 1 : 0);
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "MultiStatement");
		for (AbstractScriptNode node : subNodes) {
			node.print(indentation + ">");
		}
	}
}
