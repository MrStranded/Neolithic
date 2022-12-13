package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.scripts.Script;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.BreakException;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.utils.Logger;

public class WhileStatementNode extends AbstractScriptNode {

	public WhileStatementNode(AbstractScriptNode condition, AbstractScriptNode body) {
		subNodes = new AbstractScriptNode[2];
		subNodes[0] = condition;
		subNodes[1] = body;
	}

	@Override
	public Variable execute(Instance instance, Script script) throws ScriptInterruptedException {
		Variable body = new Variable();
		while (!subNodes[0].execute(instance, script).isNull()) { // condition
			try {
				body = subNodes[1].execute(instance, script); // body
			} catch (BreakException breakException) {
				break;
			}
		}
		return body;
	}

	@Override
	public void print(String indentation) {
		Logger.raw(indentation + "While Statement");
		subNodes[0].print(indentation + "-");
		subNodes[1].print(indentation + ".");
	}
}
