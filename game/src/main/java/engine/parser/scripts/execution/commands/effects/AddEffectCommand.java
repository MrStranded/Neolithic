package engine.parser.scripts.execution.commands.effects;

import constants.ScriptConstants;
import engine.data.Data;
import engine.data.entities.Effect;
import engine.data.entities.Instance;
import engine.data.proto.Container;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.InvalidValueException;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.ScriptContext;
import engine.parser.scripts.execution.commands.AbstractCommand;
import engine.parser.utils.Logger;

import java.util.List;

public class AddEffectCommand extends AbstractCommand {

    @Override
    protected Variable executeCommand(ScriptContext context, Variable[] parameters) throws ScriptInterruptedException {
        if (parameters.length >= 4) {
            Instance target = parameters[0].getInstance();
            String name = parameters[1].getString();
            int duration = parameters[2].getInt();
            List<Variable> attributes = parameters[3].getList();

            checkValue(context, target, "target instance");

            Effect effect = new Effect(-1);
            effect.setName(name);
            effect.setRemainingTicks(duration);
            effect.setSuperInstance(target);

            boolean getID = true;
            String attributeTextID = null;
            for (Variable variable : attributes) {
                if (getID) {
                    if (variable.getType() == DataType.ATTRIBUTE) {
                        effect.setAttribute(variable.getAttribute().getId(), variable.getAttribute().getValue());
                    } else {
                        attributeTextID = variable.getString();
                        getID = false;
                    }
                } else {
                    int id = Data.getProtoAttributeID(attributeTextID);
                    try {
                        checkAttribute(context, id, attributeTextID);
                        effect.setAttribute(id, variable.getInt());

                    } catch (InvalidValueException e) {
                        Logger.error(e);
                    }

                    getID = true;
                }
            }

            effect.run(ScriptConstants.EVENT_NEW, new Variable[] { new Variable(target) });

            target.addEffect(effect);
            return new Variable(effect);

        } else if (requireParameters(context, 2)) {
            Instance target = parameters[0].getInstance();
            Container container = parameters[1].getContainer();
            int containerId = -1;

            checkValue(context, target, "target instance");
            containerId = checkType(context, container, parameters[1].getString());

            Effect effect = new Effect(containerId);
            effect.setSuperInstance(target);

            effect.run(ScriptConstants.EVENT_NEW, new Variable[] { new Variable(target) });

            target.addEffect(effect);

            return new Variable(effect);
        }

        return null;
    }

}
