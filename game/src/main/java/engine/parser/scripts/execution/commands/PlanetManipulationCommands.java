package engine.parser.scripts.execution.commands;

import engine.data.Data;
import engine.data.variables.Variable;
import engine.logic.topology.TopologyGenerator;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.execution.Command;

import java.util.Arrays;
import java.util.List;

public class PlanetManipulationCommands implements CommandProvider {

    @Override
    public List<Command> buildCommands() {
        return Arrays.asList(
                // &&&&&&&&&&&&&&&&&&&&&&&&&&& void changeSunAngle (double angle)
                new Command(TokenConstants.CHANGE_SUN_ANGLE.getValue(), 1, (self, parameters) -> {
                    double angle = parameters[0].getDouble();
                    Data.getSun().changeAngle(angle);
                    return new Variable(0);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& int fitTiles ()
                new Command(TokenConstants.FIT_TILES.getValue(), 0, (self, parameters) -> {
                    if (Data.getPlanet() != null) {
                        TopologyGenerator.fitTiles(Data.getPlanet());
                    }
                    return new Variable();
                })
        );
    }

}
