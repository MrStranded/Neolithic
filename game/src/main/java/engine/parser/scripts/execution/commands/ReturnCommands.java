package engine.parser.scripts.execution.commands;

import engine.data.variables.Variable;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.exceptions.ReturnException;
import engine.parser.scripts.execution.Command;

import java.util.Arrays;
import java.util.List;

public class ReturnCommands implements CommandProvider {

    @Override
    public List<Command> buildCommands() {
        return Arrays.asList(
                // &&&&&&&&&&&&&&&&&&&&&&&&&&& variable return (variable)
                new Command(TokenConstants.RETURN.getValue(), 1, (self, parameters) -> {
                    throw new ReturnException(parameters[0]);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& int require (variable)
                new Command(TokenConstants.REQUIRE.getValue(), 1, (self, parameters) -> {
                    boolean notFulfilled = parameters[0].isNull();
                    if (notFulfilled) {
                        throw new ReturnException(new Variable(0));
                    }
                    return new Variable(1);
                })

        );
    }

}
