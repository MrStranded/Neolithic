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
	public void print(String indentation) {
		System.out.println(indentation + "If Statement");
		subNodes[0].print(indentation + "-");
		subNodes[1].print(indentation + ".");
		if (subNodes[2] != null) {
			subNodes[2].print(indentation + ".");
		}
	}
}