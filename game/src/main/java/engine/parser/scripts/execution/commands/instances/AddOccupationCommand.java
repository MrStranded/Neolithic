package engine.parser.scripts.execution.commands.instances;

import engine.data.Data;
import engine.data.entities.Instance;
import engine.data.proto.Container;
import engine.data.scripts.Script;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.ScriptContext;
import engine.parser.scripts.execution.commands.AbstractCommand;

import java.util.Optional;

public class AddOccupationCommand extends AbstractCommand {

    @Override
    protected Variable executeCommand(ScriptContext context, Variable[] parameters) throws ScriptInterruptedException {
        if (requireParameters(context, 2)) {
            Instance target = parameters[0].getInstance();
            int duration = parameters[1].getInt();
            Script callBackScript = null;

            checkValue(context, target, "target instance");

            if (parameters.length >= 3) {
                callBackScript = parameters[2].getScript();

                if (callBackScript == null) {
                    Optional<Container> container = Data.getContainer(target.getId());
                    callBackScript = container.map(c -> c.getScript(target.getStage(), parameters[2].getString())).orElse(null);
                }

                checkValue(context, callBackScript, "callback script");
            }

            target.addOccupation(duration, callBackScript);
        }

        return null;
    }

}
