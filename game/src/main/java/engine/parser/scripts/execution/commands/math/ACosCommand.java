package engine.parser.scripts.execution.commands.math;

import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.ScriptContext;
import engine.parser.scripts.execution.commands.AbstractCommand;

public class ACosCommand extends AbstractCommand {

    @Override
    protected Variable executeCommand(ScriptContext context, Variable[] parameters) throws ScriptInterruptedException {
        if (requireParameters(context, 1)) {
            double cos = parameters[0].getDouble();
            return new Variable(Math.acos(cos) * 180d / Math.PI);
        }

        return null;
    }

}
