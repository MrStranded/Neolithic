package engine.parser.scripts.execution.commands.instances;

import engine.data.behaviour.Occupation;
import engine.data.entities.Instance;
import engine.data.scripts.Script;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.ScriptContext;
import engine.parser.scripts.execution.commands.AbstractCommandTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;

class AddOccupationCommandTest extends AbstractCommandTest {

    @Test
    void executeCommand() throws ScriptInterruptedException {
        ScriptContext context = createContext(3);

        AddOccupationCommand addOccupationCommand = new AddOccupationCommand();

        Instance target = new Instance(2);
        Script callback = new Script("callback", "testFile", null, Collections.emptyList());

        addOccupationCommand.executeCommand(context, createParameters(target, 10, callback));

        Occupation occupation = target.getOccupations().peek();

        assertFalse(occupation.isFinished());
    }

}