package engine.parser.scripts.execution.commands.gui;

import engine.Engine;
import engine.data.entities.GuiElement;
import engine.data.variables.Variable;
import engine.parser.scripts.exceptions.ScriptInterruptedException;
import engine.parser.scripts.execution.ScriptContext;
import engine.parser.scripts.execution.commands.AbstractCommandTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddGuiCommandTest extends AbstractCommandTest {

    @BeforeAll
    static void setup() {
        Engine.loadData();
    }

    @Test
    void executeCommand() throws ScriptInterruptedException {
        ScriptContext context = createContext(2);

        AddGuiCommand addGuiCommand = new AddGuiCommand();

        GuiElement parent = new GuiElement(1);
        String textId = "gInstanceDetail";

        Variable guiElement = addGuiCommand.executeCommand(context, createParameters(parent, textId));

        assertEquals(parent, guiElement.getGuiElement().getSuperInstance());
        assertTrue(guiElement.getGuiElement().getId() > 0);
    }
}