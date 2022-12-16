package engine.parser.scripts.execution.commands.attributes;

import engine.Engine;
import engine.data.Data;
import engine.data.entities.Effect;
import engine.data.entities.Instance;
import engine.data.variables.Variable;
import engine.parser.constants.TokenType;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.ScriptContext;
import engine.parser.scripts.execution.commands.AbstractCommandTest;
import engine.parser.scripts.execution.commands.effects.AddEffectCommand;
import engine.parser.tokenization.Token;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddPersonalAttributeCommandTest extends AbstractCommandTest {

    @BeforeAll
    static void setup() {
        Engine.loadData();
    }

    @Test
    void executeCommand_byTextId() throws ScriptInterruptedException {
        ScriptContext context = createContext(3);

        AddPersonalAttributeCommand addPersonalAttributeCommand = new AddPersonalAttributeCommand();

        Instance target = new Instance(1);
        String textId = "attHealth";

        Variable attribute = addPersonalAttributeCommand.executeCommand(context, createParameters(target, textId, 20));

        assertEquals(20, target.getAttribute(Data.getProtoAttributeID(textId)).getValue());
        assertEquals(20, attribute.getAttribute().getValue());
    }

}