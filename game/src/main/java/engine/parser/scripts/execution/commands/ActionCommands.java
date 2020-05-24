package engine.parser.scripts.execution.commands;

import engine.data.entities.Instance;
import engine.data.planetary.Tile;
import engine.data.variables.Variable;
import engine.logic.topology.Pathfinding;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.execution.Command;

import java.util.Arrays;
import java.util.List;

public class ActionCommands implements CommandProvider {

    @Override
    public List<Command> buildCommands() {
        return Arrays.asList(
                // &&&&&&&&&&&&&&&&&&&&&&&&&&& tile moveTo (Instance instance, Tile tile, int steps)
                new Command(TokenConstants.MOVE_TO.getValue(), 3, (self, parameters) -> {
                    Instance instance = parameters[0].getInstance();
                    CommandUtils.checkValueExists(instance, "target instance");
                    Tile tile = parameters[1].getTile();
                    CommandUtils.checkValueExists(tile, "target tile");
                    int steps = parameters[2].getInt();

                    Tile newPosition = Pathfinding.moveTowardsTile(instance, tile, steps);
                    if (newPosition != instance.getPosition()) {
                        instance.placeInto(newPosition);
                    }

                    return new Variable(newPosition);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& instance pickUp (Instance holder, Instance item)
                new Command(TokenConstants.PICK_UP.getValue(), 2, (self, parameters) -> {
                    Instance holder = parameters[0].getInstance();
                    CommandUtils.checkValueExists(holder, "holder instance");
                    Instance item = parameters[1].getInstance();
                    CommandUtils.checkValueExists(item, "item instance");

                    item.placeInto(holder);
                    return new Variable(item);
                })

        );
    }

}
