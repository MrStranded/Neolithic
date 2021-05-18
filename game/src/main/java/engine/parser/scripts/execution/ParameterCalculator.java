package engine.parser.scripts.execution;

import engine.data.entities.Instance;
import engine.data.scripts.Script;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.ReturnException;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.nodes.AbstractScriptNode;

public class ParameterCalculator {

	public static Variable[] calculateParameters(Instance self, Script script, AbstractScriptNode node) {
		Variable[] parameters;
		if (node.getSubNodes() == null) {
			parameters = new Variable[0];
		} else {
			AbstractScriptNode[] subNodes = node.getSubNodes();
			parameters = new Variable[subNodes.length];
			for (int i=0; i<subNodes.length; i++) {
				try {
					parameters[i] = subNodes[i].execute(self, script);
				} catch (ReturnException returnException) {
					parameters[i] = returnException.getReturnValue();
				} catch (ScriptInterruptedException e) {
					parameters[i] = new Variable();
					e.printStackTrace();
				}
			}
		}
		return parameters;
	}

}
