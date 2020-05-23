package engine.parser.scripts.execution;

import engine.data.Data;
import engine.data.entities.Instance;
import engine.data.proto.Container;
import engine.data.scripts.Script;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.InvalidValueException;
import engine.parser.scripts.exceptions.NotEnoughParametersException;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.nodes.CommandExpressionNode;
import engine.parser.utils.Logger;

public class Command {

    private final String command;
    private final int minNumberOfParameters;
    private final CommandCode commandCode;

    public Command(String command, int minNumberOfParameters, CommandCode commandCode) {
        this.command = command;
        this.minNumberOfParameters = minNumberOfParameters;
        this.commandCode = commandCode;
    }

    public Variable execute(Instance self, Variable[] parameters) throws ScriptInterruptedException {
        checkNumberOfParameters(parameters);

        return commandCode.execute(self, parameters);
    }

    private void checkNumberOfParameters(Variable[] parameters) throws NotEnoughParametersException {
        if (parameters.length < minNumberOfParameters) {
            throw new NotEnoughParametersException("The command '" + command + "' requires " + minNumberOfParameters + " parameters but only received " + parameters.length);
        }
    }

    public String getName() {
        return command;
    }

    @FunctionalInterface
    public interface CommandCode {
        Variable execute(Instance self, Variable[] parameters) throws ScriptInterruptedException;
    }

}
