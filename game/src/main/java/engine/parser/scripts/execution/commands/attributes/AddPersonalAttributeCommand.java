package engine.parser.scripts.execution.commands.attributes;

import constants.ScriptConstants;
import engine.data.Data;
import engine.data.entities.GuiElement;
import engine.data.entities.Instance;
import engine.data.proto.Container;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.ScriptContext;
import engine.parser.scripts.execution.commands.AbstractCommand;

import java.util.Arrays;

public class AddPersonalAttributeCommand extends AbstractCommand {

    @Override
    protected Variable executeCommand(ScriptContext context, Variable[] parameters) throws ScriptInterruptedException {
        if (requireParameters(context, 3)) {
            Instance target = parameters[0].getInstance();
            String attributeTextID = parameters[1].getString();
            int amount = parameters[2].getInt();
            int attributeID = Data.getProtoAttributeID(attributeTextID);

            checkValue(context, target, "target instance");
            checkAttribute(context, attributeID, attributeTextID);

            target.addAttribute(attributeID, amount);

            return new Variable(target.getAttribute(attributeID));
        }

        return null;
    }

}
