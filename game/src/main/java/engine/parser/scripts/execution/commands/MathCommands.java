package engine.parser.scripts.execution.commands;

import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.execution.Command;

import java.util.Arrays;
import java.util.List;

public class MathCommands implements CommandProvider {

    @Override
    public List<Command> buildCommands() {
        return Arrays.asList(
                // &&&&&&&&&&&&&&&&&&&&&&&&&&& double abs (double value)
                new Command(TokenConstants.ABSOLUTE.getValue(), 1, (self, parameters) -> {
                    double value = parameters[0].getDouble();
                    return new Variable(Math.abs(value));
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& int ceil (double value)
                new Command(TokenConstants.CEIL.getValue(), 1, (self, parameters) -> new Variable(Math.ceil(parameters[0].getDouble()))),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& double chance (double probability)
                new Command(TokenConstants.CHANCE.getValue(), 1, (self, parameters) -> {
                    double chance = parameters[0].getDouble();
                    return new Variable(Math.random() < chance ? 1 : 0);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& int floor (double value)
                new Command(TokenConstants.FLOOR.getValue(), 1, (self, parameters) -> new Variable(Math.floor(parameters[0].getDouble()))),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& double max (double d1, double d2, ...)
                new Command(TokenConstants.MAX.getValue(), 1, (self, parameters) -> {
                    return new Variable(getExtreme((first, second) -> first > second, parameters));
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& double min (double d1, double d2, ...)
                new Command(TokenConstants.MIN.getValue(), 1, (self, parameters) -> {
                    return new Variable(getExtreme((first, second) -> first < second, parameters));
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& int random ([int bottom,] int top)
                new Command(TokenConstants.RANDOM.getValue(), 1, (self, parameters) -> {
                    if (parameters.length >= 2) {
                        double bottom = parameters[0].getDouble();
                        double top = parameters[1].getDouble();

                        int value = (int) (bottom + Math.random() * (top - bottom));
                        return new Variable(value);
                    }

                    double top = parameters[0].getDouble();

                    int value = (int) (Math.random() * top);
                    return new Variable(value);
                })
        );
    }

    private static double getExtreme(ExtremeFinder finder, Variable[] variables) {
        double extreme = 0;
        boolean first = true;

        for (Variable variable : variables) {
            if (variable.getType() == DataType.LIST) {
                for (Variable sub : variable.getList()) {
                    if (first || finder.extremerThan(sub.getDouble(), extreme)) {
                        extreme = sub.getDouble();
                        first = false;
                    }
                }
            } else {
                if (first || finder.extremerThan(variable.getDouble(), extreme)) {
                    extreme = variable.getDouble();
                    first = false;
                }
            }
        }

        return extreme;
    }

    private interface ExtremeFinder {
        boolean extremerThan(double first, double second);
    }

}
