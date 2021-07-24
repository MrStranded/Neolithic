package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.scripts.Script;
import engine.data.variables.Variable;
import engine.parser.scripts.execution.ParameterCalculator;
import engine.parser.tokenization.Token;

import java.util.List;

public class ScriptCallNode extends AbstractScriptNode {

	private Token identifier;
	private Variable target = null;

	public ScriptCallNode(Token identifier, List<AbstractScriptNode> parameters) {
		this.identifier = identifier;

		subNodes = new AbstractScriptNode[parameters.size()];
		int i=0;
		for (AbstractScriptNode node : parameters) {
			subNodes[i++] = node;
		}
	}

	@Override
	public Variable execute(Instance instance, Script script) {
		Instance targetInstance = instance;
		Variable result;

		// running the script on a specific target instead of self
		if (target != null) {
			targetInstance = target.getInstance();
		}

		result = targetInstance.run(identifier.getValue(), ParameterCalculator.calculateParameters(instance, script, this));

		target = null; // clean up, so the garbage collector can remove the instance
		return result;
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "ScriptCall: " + identifier);
		for (AbstractScriptNode node : subNodes) {
			node.print(indentation + " ");
		}
	}

	public void setTarget(Variable target) {
		this.target = target;
	}
}
