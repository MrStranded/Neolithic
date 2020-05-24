package engine.parser.scripts.execution;

import engine.data.entities.Instance;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.NotEnoughParametersException;
import engine.parser.scripts.exceptions.ScriptInterruptedException;

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
            throw new NotEnoughParametersException("The command '" + command + "' requires at least " + minNumberOfParameters + " " +
                    "parameters but only received " + parameters.length);
        }
    }

    public String getName() {
        return command;
    }

    @Override
    public String toString() {
        return command;
    }

    @FunctionalInterface
    public interface CommandCode {
        Variable execute(Instance self, Variable[] parameters) throws ScriptInterruptedException;
    }

}
