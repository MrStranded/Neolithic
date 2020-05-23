package engine.parser.scripts.execution.commands;

import engine.data.variables.Variable;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.execution.Command;

import java.util.Arrays;
import java.util.List;

public class ListCommands implements CommandProvider {

    @Override
    public List<Command> buildCommands() {
        return Arrays.asList(
                // &&&&&&&&&&&&&&&&&&&&&&&&&&& int length (List list)
                new Command(TokenConstants.LENGTH.getValue(), 1, (self, parameters) -> {
                    List<Variable> list = parameters[0].getList();
                    CommandUtils.checkValueExists(list, "list");
                    return new Variable(list.size());
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& boolean contains (List list, Variable element)
                new Command(TokenConstants.CONTAINS.getValue(), 2, (self, parameters) -> {
                    List<Variable> list = parameters[0].getList();
                    Variable element = parameters[1];

                    CommandUtils.checkValueExists(list, "list");
                    CommandUtils.checkValueExists(element, "search element");

                    for (Variable variable : list) {
                        if (variable != null && variable.equals(element)) {
                            return new Variable(1);
                        }
                    }

                    return new Variable(0);
                })

        );
    }
}
