package engine.parser.scripts.execution.commands.math;

import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.ScriptContext;
import engine.parser.scripts.execution.commands.AbstractCommandTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ACosCommandTest extends AbstractCommandTest {

    @Test
    void executeCommand() throws ScriptInterruptedException {
        ScriptContext context = createContext(1);

        ACosCommand acosCommand = new ACosCommand();

        assertEquals(0, acosCommand.executeCommand(context, createParameters(1.0)).getDouble());
        assertEquals(60, acosCommand.executeCommand(context, createParameters(0.5)).getDouble(), 0.001);
        assertEquals(90, acosCommand.executeCommand(context, createParameters(0)).getDouble());
        assertEquals(180, acosCommand.executeCommand(context, createParameters(-1.0)).getDouble());
    }
}