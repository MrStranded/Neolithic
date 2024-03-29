package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.scripts.Script;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.BreakException;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.utils.Logger;

public class BreakStatementNode extends AbstractScriptNode {

	public BreakStatementNode() {}

	@Override
	public Variable execute(Instance instance, Script script) throws ScriptInterruptedException {
		throw new BreakException();
	}

	@Override
	public void print(String indentation) {
		Logger.raw(indentation + "Break");
	}
}
