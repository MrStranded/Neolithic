package engine.parser.scripts.execution.commands.effects;

import engine.Engine;
import engine.data.Data;
import engine.data.entities.Effect;
import engine.data.entities.Instance;
import engine.data.variables.Variable;
import engine.parser.constants.TokenType;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.ScriptContext;
import engine.parser.scripts.execution.commands.AbstractCommandTest;
import engine.parser.tokenization.Token;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddEffectCommandTest extends AbstractCommandTest {

    @BeforeAll
    static void setup() {
        Engine.loadData();
    }

    @Test
    void executeCommand_byTextId() throws ScriptInterruptedException {
        ScriptContext context = createContext(2, new Token(TokenType.COMMAND, "addEffect", 0));

        AddEffectCommand addEffectCommand = new AddEffectCommand();

        Instance target = new Instance(42);
        String textId = "effShadow";

        Variable effect = addEffectCommand.executeCommand(context, createParameters(target, textId));

        assertEquals(target, effect.getInstance().getSuperInstance());
        assertTrue(effect.getInstance().getId() > 0);
    }

    @Test
    void executeCommand_byValues() throws ScriptInterruptedException {
        ScriptContext context = createContext(4, new Token(TokenType.COMMAND, "addEffect", 0));

        AddEffectCommand addEffectCommand = new AddEffectCommand();

        Instance target = new Instance(42);

        List<Variable> valueList = Arrays.asList(
                new Variable("attHealth"), new Variable(5),
                new Variable("attAge"), new Variable(-10)
        );

        Variable effect = addEffectCommand.executeCommand(context, createParameters(target, "Effect name", 7, valueList));

        assertEquals(target, effect.getInstance().getSuperInstance());
        assertEquals(-1, effect.getInstance().getId());
        assertEquals(5, effect.getInstance().getAttributeValue(Data.getProtoAttributeID("attHealth")));
        assertEquals(-10, effect.getInstance().getAttributeValue(Data.getProtoAttributeID("attAge")));
        assertEquals(7, ((Effect) effect.getInstance()).getRemainingTicks());
    }
}