package engine.parser.scripts.execution.commands.gui;

import constants.ScriptConstants;
import engine.data.entities.GuiElement;
import engine.data.proto.Container;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.ScriptContext;
import engine.parser.scripts.execution.commands.AbstractCommand;

import java.util.Arrays;

public class AddGuiCommand extends AbstractCommand {

    @Override
    protected Variable executeCommand(ScriptContext context, Variable[] parameters) throws ScriptInterruptedException {
        if (requireParameters(context, 2)) {
            GuiElement parent = parameters[0].getGuiElement();
            Container container = parameters[1].getContainer();

            checkValue(context, parent, "gui parent element");

            int containerId = checkType(context, container, parameters[1].getString());

            GuiElement element = new GuiElement(containerId);
            element.setGuiParent(parent);

            if (parameters.length > 2) {
                element.run(ScriptConstants.EVENT_NEW, Arrays.copyOfRange(parameters, 2, parameters.length));
            } else {
                element.run(ScriptConstants.EVENT_NEW, new Variable[] {});
            }

            return new Variable(element);
        }

        return null;
    }

}
