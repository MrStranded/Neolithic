package engine.parser.scripts.nodes;

import engine.data.Data;
import engine.data.scripts.Script;
import engine.data.entities.Instance;
import engine.data.variables.Variable;

public class MainNode extends AbstractScriptNode {

	public MainNode() {}

	@Override
	public Variable execute(Instance instance, Script script) {
		return new Variable(Data.getMainInstance());
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "Main");
	}
}
