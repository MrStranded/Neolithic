package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.Script;
import engine.data.identifiers.AttributeIdentifier;
import engine.data.variables.Variable;
import engine.parser.tokenization.Token;

public class IdentifierNode extends AbstractScriptNode {

	private Token identifier;
	private Variable target = null;
	private AttributeIdentifier attributeIdentifier = null;

	public IdentifierNode(Token identifier) {
		this.identifier = identifier;
	}

	@Override
	public Variable execute(Instance instance, Script script) {
		Variable variable;
		Instance targetInstance = getTargetInstance();

		if (attributeIdentifier == null) { // ----- retrieve variable
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
		} else { // ------------------------------- retrive attribute
			if (targetInstance != null) { // return attribute from target instance
				variable = new Variable(attributeIdentifier.retrieve(targetInstance));
			} else { // return variable from self
				variable = new Variable(attributeIdentifier.retrieve(instance));
			}
		}

		return variable;
	}

	public void markAsAttributeIdentifier() {
		attributeIdentifier = new AttributeIdentifier(identifier.getValue());
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
