package engine.parser.scripts.execution.commands.math;

import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.ScriptContext;
import engine.parser.scripts.execution.commands.AbstractCommandTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ATanCommandTest extends AbstractCommandTest {

    @Test
    void executeCommand() throws ScriptInterruptedException {
        ScriptContext context = createContext(1);

        ATanCommand aTanCommand = new ATanCommand();

        assertEquals(45, aTanCommand.executeCommand(context, createParameters(1.0)).getDouble(), 0.001);
        assertEquals(26.565, aTanCommand.executeCommand(context, createParameters(0.5)).getDouble(), 0.001);
        assertEquals(0, aTanCommand.executeCommand(context, createParameters(0)).getDouble());
        assertEquals(-45, aTanCommand.executeCommand(context, createParameters(-1.0)).getDouble(), 0.001);
    }

}
