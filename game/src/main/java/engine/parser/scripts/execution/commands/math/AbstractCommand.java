package engine.parser.scripts.execution.commands.math;

import engine.data.Data;
import engine.data.proto.Container;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.InvalidValueException;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.ScriptContext;
import engine.parser.utils.Logger;

public abstract class AbstractCommand {

    public Variable execute(ScriptContext context, Variable[] parameters)
            throws ScriptInterruptedException {
        Variable result = executeCommand(context, parameters);

        return result != null ? result : new Variable();
    }

    protected abstract Variable executeCommand(ScriptContext context, Variable[] parameters)
            throws ScriptInterruptedException;

    protected void checkValue(ScriptContext context, Object value, String objectName) throws InvalidValueException {
        if (value == null) {
            Logger.executionError(
                    "Value '" + objectName + "' is empty!",
                    context.getCommandNode().getCommand(), context.getScript());
            throw new InvalidValueException("Value '" + objectName + "' is invalid!");
        }
    }

    protected void checkAttribute(ScriptContext context, int attributeID, String attributeTextID) throws InvalidValueException {
        if (attributeID == -1) {
            Logger.executionError(
                    "Attribute '" + attributeTextID + "' does not exist!",
                    context.getCommandNode().getCommand(), context.getScript());
            throw new InvalidValueException("Attribute '" + attributeTextID + "' does not exist!");
        }
    }

    protected int checkType(ScriptContext context, Container type, String typeName) throws InvalidValueException {
        int containerID = -1;

        if ((type == null) || ((containerID = Data.getContainerID(type.getTextID())) < 0)) {
            Logger.executionError(
                    "Type with name '" + typeName + "' does not exist!",
                    context.getCommandNode().getCommand(), context.getScript());
            throw new InvalidValueException("Type with name '" + typeName + "' does not exist!");
        }

        return containerID;
    }

    protected boolean requireParameters(ScriptContext context, int amount) {
        if (amount > 0 && (context.getCommandNode().getSubNodes() == null || amount > context.getCommandNode().getSubNodes().length)) {

            Logger.executionError(
                    "The command '" + context.getCommandNode().getCommand().getValue() + "' needs at least " + amount + " parameters!",
                    context.getCommandNode().getCommand());
            return false;
        }
        return true;
    }

}
