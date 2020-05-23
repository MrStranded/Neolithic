package engine.parser.scripts.nodes;

import engine.data.entities.Instance;
import engine.data.scripts.Script;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.InvalidValueException;
import engine.parser.scripts.exceptions.NotEnoughParametersException;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.CommandExecutor;
import engine.parser.tokenization.Token;
import engine.parser.utils.Logger;

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
			return CommandExecutor.executeCommand(instance, script, this);
		} catch (InvalidValueException e) {
			Logger.error("Command '" + getCommand().getValue() + "' received an input parameter of the wrong type " +
					"and could not be executed on line " + getCommand().getLine() + " " +
					"in script " + script.getTextId() + " in file " + script.getFileName());
		} catch (NotEnoughParametersException e) {
			Logger.error("Command '" + getCommand().getValue() + "' did not receive enough parameters " +
					"and could not be executed on line " + getCommand().getLine() + " " +
					"in script " + script.getTextId() + " in file " + script.getFileName());
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
