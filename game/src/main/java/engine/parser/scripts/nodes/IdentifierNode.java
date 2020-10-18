package engine.parser.scripts.nodes;

import engine.data.Data;
import engine.data.scripts.Script;
import engine.data.entities.Instance;
import engine.data.identifiers.AttributeIdentifier;
import engine.data.proto.Container;
import engine.data.variables.Variable;
import engine.parser.tokenization.Token;

public class IdentifierNode extends AbstractScriptNode {

	private Token identifier;
	private Variable target = null;
	private AttributeIdentifier attributeIdentifier = null;
	private boolean retrieveCompleteAttributeValue = false;

	public IdentifierNode(Token identifier) {
		this.identifier = identifier;
	}

	@Override
	public Variable execute(Instance instance, Script script) {
		Variable variable;
		Instance targetInstance = getTargetInstance();
		Container targetContainer = getTargetContainer();

		if (attributeIdentifier == null) { // ----- retrieve variable
			if (targetInstance != null) { // return variable from an instance
				variable = targetInstance.getVariable(identifier.getValue());
				if (variable == null) { // variable does not yet exist in instance -> create it
					variable = Variable.withName(identifier.getValue());
					targetInstance.addVariable(variable);
				}
			} else if (targetContainer != null) {
				Script targetScript = targetContainer.getScript(instance.getStage(), identifier.getValue());
				if (targetScript != null) {
					variable = new Variable(targetScript);
				} else { // script does not exist -> just feed the string value of the identifier into the variable
					variable = new Variable(identifier.getValue());
				}
			} else { // retrieve variable from current script scope OR retrieve container from "global" scope
				variable = script.getVariable(identifier.getValue());
				if (variable == null) { // script scope does not contain variable -> search through containers
					int containerID = Data.getContainerID(identifier.getValue());
					if (containerID >= 0) { // fill container into variable
						return new Variable(Data.getContainer(containerID));
					} else { // create new variable in scope
						variable = Variable.withName(identifier.getValue());
						script.addVariable(variable);
					}
				}
			}
		} else { // ------------------------------- retrive attribute
			if (targetInstance != null) { // return attribute from target instance
				if (retrieveCompleteAttributeValue) {
					variable = new Variable(attributeIdentifier.retrieveAll(targetInstance));
				} else {
					variable = new Variable(attributeIdentifier.retrieve(targetInstance));
				}
			} else if (targetContainer != null) { // return attribute from target container
				variable = new Variable(attributeIdentifier.retrieve(targetContainer));
			} else { // return variable from self
				if (retrieveCompleteAttributeValue) {
					variable = new Variable(attributeIdentifier.retrieveAll(targetInstance));
				} else {
					variable = new Variable(attributeIdentifier.retrieve(instance));
				}
			}
		}

		target = null; // clean up, so the garbage collector can remove the instance
		return variable;
	}

	public void markAsAttributeIdentifier() {
		attributeIdentifier = new AttributeIdentifier(identifier.getValue());
	}

	public void setRetrieveCompleteAttributeValue(boolean retrieveCompleteAttributeValue) {
		this.retrieveCompleteAttributeValue = retrieveCompleteAttributeValue;
	}

	public Instance getTargetInstance() {
		if (target != null) {
			return target.getInstance();
		}
		return null;
	}
	public Container getTargetContainer() {
		if (target != null) {
			return target.getContainer();
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
