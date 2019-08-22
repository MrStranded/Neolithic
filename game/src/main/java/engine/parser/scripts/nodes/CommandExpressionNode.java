package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.scripts.Script;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.InvalidValueException;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.CommandExecuter;
import engine.parser.tokenization.Token;

import java.util.List;

public class CommandExpressionNode extends AbstractScriptNode {

	private Token command;

	public CommandExpressionNode(Token command, List<AbstractScriptNode> parameters) {
		this.command = command;

		subNodes = new AbstractScriptNode[parameters.size()];
		int i=0;
		for (AbstractScriptNode node : parameters) {
			subNodes[i++] = node;
		}
	}

	@Override
	public Variable execute(Instance instance, Script script) throws ScriptInterruptedException {
		try {
			return CommandExecuter.executeCommand(instance, script, this);
		} catch (InvalidValueException e) {
			// the execution of the command did not work due to invalid input parameters -> do nothing
		}
		return new Variable();
	}

	@Override
	public void print(String indentation) {
		System.out.println(indentation + "Command: " + command);
		for (AbstractScriptNode node : subNodes) {
			node.print(indentation + " ");
		}
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public Token getCommand() {
		return command;
	}
}
