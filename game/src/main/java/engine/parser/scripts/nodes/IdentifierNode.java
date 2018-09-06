package engine.parser.scripts.nodes;

import engine.data.attributes.Attribute;
import engine.data.entities.Instance;
import engine.data.proto.Data;
import engine.data.structures.Script;
import engine.data.variables.Variable;
import engine.parser.tokenization.Token;

public class IdentifierNode extends AbstractScriptNode {

	private Token identifier;
	private Variable target = null;

	public IdentifierNode(Token identifier) {
		this.identifier = identifier;
	}

	@Override
	public Variable execute(Instance instance, Script script) {
		Variable variable;
		Instance targetInstance = getTargetInstance();

		if (targetInstance != null) { // return variable from an instance
			variable = targetInstance.getVariable(identifier.getValue());
			if (variable == null) {
				variable = Variable.withName(identifier.getValue());
				targetInstance.addVariable(variable);
			}
		} else { // retrieve variable from current script scope
			variable = script.getVariable(identifier.getValue());
			if (variable == null) {
				variable = Variable.withName(identifier.getValue());
				script.addVariable(variable);
			}
		}

		return variable;
	}

	public Instance getTargetInstance() {
		if (target != null) {
			return target.getInstance();
		}
		return null;
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "Identifier: " + identifier);
	}

	public void setTarget(Variable target) {
		this.target = target;
	}
}
