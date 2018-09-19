package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.Script;
import engine.data.variables.Variable;

public class WhileStatementNode extends AbstractScriptNode {

	public WhileStatementNode(AbstractScriptNode condition, AbstractScriptNode body) {
		subNodes = new AbstractScriptNode[2];
		subNodes[0] = condition;
		subNodes[1] = body;
	}

	@Override
	public Variable execute(Instance instance, Script script) {
		Variable body = new Variable();
		while (!subNodes[0].execute(instance, script).isNull()) { // condition
			body = subNodes[1].execute(instance, script); // body
		}
		return body;
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "While Statement");
		subNodes[0].print(indentation + "-");
		subNodes[1].print(indentation + ".");
	}
}
