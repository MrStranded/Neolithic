package engine.parser.scripts.execution.commands;

import engine.data.variables.Variable;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.execution.Command;
import engine.parser.utils.Logger;

import java.util.Arrays;
import java.util.List;

public class GuiCommands implements CommandProvider {

    @Override
    public List<Command> buildCommands() {
        return Arrays.asList(
                // &&&&&&&&&&&&&&&&&&&&&&&&&&& string print (String text)
                new Command(TokenConstants.PRINT.getValue(), 1, (self, parameters) -> {
                    String text = parameters[0].getString();
                    Logger.log("PRINT: " + text);
                    return new Variable(text);
                })

        );
    }

}
