package engine.parser.scripts.execution;

import engine.data.entities.Instance;
import engine.data.structures.Script;
import engine.data.variables.Variable;
import engine.parser.scripts.nodes.AbstractScriptNode;
import engine.parser.scripts.nodes.CommandExpressionNode;

public class ParameterCalculator {

	public static Variable[] calculateParameters(Instance self, Script script, AbstractScriptNode node) {
		Variable[] parameters;
		if (node.getSubNodes() == null) {
			parameters = new Variable[0];
		} else {
			AbstractScriptNode[] subNodes = node.getSubNodes();
			parameters = new Variable[subNodes.length];
			for (int i=0; i<subNodes.length; i++) {
				parameters[i] = subNodes[i].execute(self, script);
			}
		}
		return parameters;
	}

}
