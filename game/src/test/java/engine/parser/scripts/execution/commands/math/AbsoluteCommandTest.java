package engine.parser.scripts.execution.commands.math;

import engine.data.entities.Instance;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.ScriptContext;
import engine.parser.scripts.nodes.CommandExpressionNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbsoluteCommandTest extends AbstractCommandTest {

    @Test
    void executeCommand() throws ScriptInterruptedException {
        ScriptContext context = createContext(1);

        AbsoluteCommand absoluteCommand = new AbsoluteCommand();

        assertEquals(10, absoluteCommand.executeCommand(context, createParameters(10)).getInt());
        assertEquals(0, absoluteCommand.executeCommand(context, createParameters(-0)).getInt());
        assertEquals(69, absoluteCommand.executeCommand(context, createParameters(-69)).getInt());
        assertEquals(100.5, absoluteCommand.executeCommand(context, createParameters(100.5d)).getDouble());
        assertEquals(1.25, absoluteCommand.executeCommand(context, createParameters(-1.25d)).getDouble());
        assertEquals(0, absoluteCommand.executeCommand(context, createParameters(new Instance(123))).getInt());
    }
}