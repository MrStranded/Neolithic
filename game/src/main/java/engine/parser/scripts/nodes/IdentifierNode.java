package engine.parser.scripts.nodes;

import engine.data.attributes.Attribute;
import engine.data.entities.Instance;
import engine.data.proto.Data;
import engine.data.variables.Variable;
import engine.parser.tokenization.Token;

public class IdentifierNode extends AbstractScriptNode {

	private Token identifier;
	private Variable target;

	public IdentifierNode(Token identifier) {
		this.identifier = identifier;
	}

	@Override
	public Variable execute(Instance instance) {
		Variable variable = null;
		Instance targetInstance = null;
		if (target != null) {
			targetInstance = target.getInstance();
		}

		if (targetInstance != null) { // get value from other instance
			variable = targetInstance.getVariable(identifier.getValue());
		} else if (instance != null) { // get value from self
			variable = instance.getVariable(identifier.getValue());
		}

		if (variable != null) {
			return variable;
		} else {
			return Variable.withName(identifier.getValue());
		}
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "Identifier: " + identifier);
	}

	public void setTarget(Variable target) {
		this.target = target;
	}
}
