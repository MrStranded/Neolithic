package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.Script;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.ScriptInterruptedException;

public abstract class AbstractScriptNode {

	protected AbstractScriptNode[] subNodes;

	public abstract Variable execute(Instance instance, Script script) throws ScriptInterruptedException;

	public abstract void print(String indentation);

	public AbstractScriptNode[] getSubNodes() {
		return subNodes;
	}
}
