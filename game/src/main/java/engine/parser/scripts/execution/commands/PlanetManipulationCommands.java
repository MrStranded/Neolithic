package engine.parser.scripts.execution.commands;

import constants.TopologyConstants;
import engine.data.Data;
import engine.data.planetary.Face;
import engine.data.planetary.Planet;
import engine.data.planetary.Tile;
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
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& int setHeight (int height)
                new Command(TokenConstants.SET_HEIGHT.getValue(), 2, (self, parameters) -> {
                    Tile tile = parameters[0].getTile();
                    CommandUtils.checkValueExists(tile, "target tile");
                    int height = parameters[1].getInt();

                    height = getBoundedHeight(height);
                    tile.setHeight(height);

                    return new Variable(height);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& string setSunAngle (double angle)
                new Command(TokenConstants.SET_SUN_ANGLE.getValue(), 1, (self, parameters) -> {
                    double angle = parameters[0].getDouble();
                    Data.getSun().setAngle(angle);
                    return new Variable(angle);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& int setWaterHeight (int height)
                new Command(TokenConstants.SET_WATER_HEIGHT.getValue(), 2, (self, parameters) -> {
                    Tile tile = parameters[0].getTile();
                    CommandUtils.checkValueExists(tile, "target tile");
                    int height = parameters[1].getInt();

                    height = getBoundedHeight(height);
                    tile.setWaterHeight(height);

                    return new Variable(height);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& int setWaterLevel (int level)
                new Command(TokenConstants.SET_WATER_LEVEL.getValue(), 1, (self, parameters) -> {
                    Planet planet = Data.getPlanet();
                    int level = (int) (parameters[0].getDouble());
                    level = getBoundedHeight(level);

                    if (planet != null) {
                        for (Face face : planet.getFaces()) {
                            for (Tile tile : face.getTiles()) {
                                tile.setWaterHeight(level);
                            }
                        }
                        return new Variable(level);
                    }

                    return new Variable();
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& void updatePlanetMesh ()
                new Command(TokenConstants.UPDATE_PLANET_MESH.getValue(), 0, (self, parameters) -> {
                    Data.setUpdatePlanetMesh(true);
                    return new Variable("Semira <3");
                })

        );
    }

    private int getBoundedHeight(int height) {
        if (height < 0) { height = 0; }
        if (height > TopologyConstants.PLANET_MAXIMUM_HEIGHT) { height = (int) TopologyConstants.PLANET_MAXIMUM_HEIGHT; }
        return height;
    }

}
