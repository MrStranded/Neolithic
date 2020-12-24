package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.scripts.Script;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.ReturnException;
import engine.parser.scripts.exceptions.ScriptInterruptedException;

public class ReturnStatementNode extends AbstractScriptNode {

	public ReturnStatementNode() { }
	public ReturnStatementNode(AbstractScriptNode value) {
		subNodes = new AbstractScriptNode[1];
		subNodes[0] = value;
	}

	@Override
	public Variable execute(Instance instance, Script script) throws ScriptInterruptedException {
		if (subNodes == null) { throw new ReturnException(); }

		throw new ReturnException(subNodes[0].execute(instance, script));
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "Return Statement");
		subNodes[0].print(indentation + "-");
	}
}
