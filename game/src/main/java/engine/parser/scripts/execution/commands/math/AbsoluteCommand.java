package engine.parser.scripts.execution.commands.math;

import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.ScriptContext;

public class AbsoluteCommand extends AbstractCommand {

    @Override
    protected Variable executeCommand(ScriptContext context, Variable[] parameters) throws ScriptInterruptedException {
        if (requireParameters(context, 1)) {
            double value = parameters[0].getDouble();
            return new Variable(Math.abs(value));
        }

        return null;
    }

}
