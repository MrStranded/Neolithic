package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.variables.Variable;

public abstract class AbstractScriptNode {

	protected AbstractScriptNode[] subNodes;

	public abstract Variable execute(Instance instance);

	public abstract void print(String indentation);

	public AbstractScriptNode[] getSubNodes() {
		return subNodes;
	}
}
