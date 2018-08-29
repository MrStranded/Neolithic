package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.variables.Variable;

public class IfStatementNode extends AbstractScriptNode {

	public IfStatementNode(AbstractScriptNode condition, AbstractScriptNode body, AbstractScriptNode elseBody) {
		subNodes = new AbstractScriptNode[3];
		subNodes[0] = condition;
		subNodes[1] = body;
		subNodes[2] = elseBody;
	}

	@Override
	public Variable execute(Instance instance) {

		return null;
	}

	@Override
	public void print() {
		System.out.println("If Statement");
	}
}
