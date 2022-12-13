package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.scripts.Script;
import engine.data.variables.Variable;
import engine.parser.utils.Logger;

public class SelfNode extends AbstractScriptNode {

	public SelfNode() {}

	@Override
	public Variable execute(Instance instance, Script script) {
		return new Variable(instance);
	}

	@Override
	public void print(String indentation) {
		Logger.raw(indentation + "Self");
	}
}
