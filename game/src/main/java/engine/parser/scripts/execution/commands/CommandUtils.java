package engine.parser.scripts.execution.commands;

import engine.data.Data;
import engine.data.proto.Container;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.InvalidValueException;
import engine.parser.scripts.exceptions.ScriptInterruptedException;

public class CommandUtils {

    public static void checkValueExists(Object value, String objectName) throws InvalidValueException {
        if (value == null) {
            throw new InvalidValueException("Value '" + objectName + "' is empty!");
        }
    }

    public static void checkAttribute(int attributeID, String attributeTextID) throws InvalidValueException {
        if (attributeID == -1) {
            throw new InvalidValueException("Attribute '" + attributeTextID + "' does not exist!");
        }
    }

    public static int getAndCheckContainerID(Variable containerVariable) throws InvalidValueException {
        Container container = containerVariable.getContainer();
        String typeTextID = container != null ? container.getTextID() : containerVariable.getString();

        checkValueExists(typeTextID, "type");

        int containerID = Data.getContainerID(typeTextID);
        if (containerID < 0) {
            throw new InvalidValueException("Type with name '" + typeTextID + "' does not exist!");
        }

        return containerID;
    }

    public static int getAndCheckAttributeID(Variable textIDVariable) throws ScriptInterruptedException {
        String attributeTextID = textIDVariable.getString();
        int attributeID = Data.getProtoAttributeID(attributeTextID);

        CommandUtils.checkAttribute(attributeID, attributeTextID);

        return attributeID;
    }

}
