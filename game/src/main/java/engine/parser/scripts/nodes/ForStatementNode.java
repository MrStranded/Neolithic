package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.structures.Script;
import engine.data.variables.Variable;

public class ForStatementNode extends AbstractScriptNode {

	public ForStatementNode(AbstractScriptNode initial, AbstractScriptNode condition, AbstractScriptNode step, AbstractScriptNode body) {
		subNodes = new AbstractScriptNode[4];
		subNodes[0] = initial;
		subNodes[1] = condition;
		subNodes[2] = step;
		subNodes[3] = body;
	}

	@Override
	public Variable execute(Instance instance, Script script) {
		Variable body = new Variable();
		subNodes[0].execute(instance, script); // initial
		while (!subNodes[1].execute(instance, script).isNull()) { // condition
			body = subNodes[3].execute(instance, script); // body
			subNodes[2].execute(instance, script); // step
		}
		return body;
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "For Statement");
		subNodes[0].print(indentation + "-");
		subNodes[1].print(indentation + "-");
		subNodes[2].print(indentation + "-");
		subNodes[3].print(indentation + ".");
	}
}
