package engine.parser.scripts.nodes;

import engine.data.Data;
import engine.data.entities.Instance;
import engine.data.scripts.Script;
import engine.data.variables.Variable;

public class NoOperatorNode extends AbstractScriptNode {

	public NoOperatorNode() {}

	@Override
	public Variable execute(Instance instance, Script script) {
		return new Variable();
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "NOP");
	}
}
