package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.scripts.Script;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.utils.Logger;

public class IfStatementNode extends AbstractScriptNode {

	public IfStatementNode(AbstractScriptNode condition, AbstractScriptNode body, AbstractScriptNode elseBody) {
		subNodes = new AbstractScriptNode[3];
		subNodes[0] = condition;
		subNodes[1] = body;
		subNodes[2] = elseBody;
	}

	@Override
	public Variable execute(Instance instance, Script script) throws ScriptInterruptedException {
		Variable condition = subNodes[0].execute(instance, script);

		if (! condition.isNull()) {
			return subNodes[1].execute(instance, script);
		} else {
			if (subNodes[2] != null) {
				return subNodes[2].execute(instance, script);
			}
		}

		return new Variable(0);
	}

	@Override
	public void print(String indentation) {
		Logger.raw(indentation + "If Statement");
		subNodes[0].print(indentation + "-");
		subNodes[1].print(indentation + ".");
		if (subNodes[2] != null) {
			subNodes[2].print(indentation + ".");
		}
	}
}
