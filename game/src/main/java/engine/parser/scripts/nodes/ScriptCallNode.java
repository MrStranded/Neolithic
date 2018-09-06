package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.structures.Script;
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
		Instance targetInstance = null;
		if (target != null) {
			targetInstance = target.getInstance();
		}

		if (targetInstance != null) { // run script on different target
			targetInstance.runScript(identifier.getValue(), ParameterCalculator.calculateParameters(instance, script, this));
		} else { // run script on self
			instance.runScript(identifier.getValue(), ParameterCalculator.calculateParameters(instance, script, this));
		}
		return new Variable();
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