package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.structures.Script;
import engine.data.variables.Variable;
import engine.parser.tokenization.Token;

public class SelfNode extends AbstractScriptNode {

	public SelfNode() {}

	@Override
	public Variable execute(Instance instance, Script script) {
		return new Variable(instance);
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "Self");
	}
}
