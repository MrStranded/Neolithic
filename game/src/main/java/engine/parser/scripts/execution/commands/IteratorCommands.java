package engine.parser.scripts.execution.commands;

import engine.data.Data;
import engine.data.entities.Instance;
import engine.data.planetary.Face;
import engine.data.planetary.Planet;
import engine.data.planetary.Tile;
import engine.data.proto.Container;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.parser.constants.TokenConstants;
import engine.parser.scripts.execution.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IteratorCommands implements CommandProvider {

    @Override
    public List<Command> buildCommands() {
        return Arrays.asList(

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& list eachTile ()
                new Command(TokenConstants.EACH_TILE.getValue(), 0, (self, parameters) -> {
                    Planet planet = Data.getPlanet();
                    CommandUtils.checkValueExists(planet, "planet");

                    List<Variable> tileList = new ArrayList<>(planet.getSize() * planet.getSize() * 20);
                    for (Face face : planet.getFaces()) {
                        for (Tile tile : face.getTiles()) {
                            tileList.add(new Variable(tile));
                        }
                    }
                    return new Variable(tileList);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& list eachEntity ()
                new Command(TokenConstants.EACH_ENTITY.getValue(), 0, (self, parameters) -> {
                    List<Variable> entities = new ArrayList<>(Data.getPublicInstanceList().size()/2);
                    for (Instance instance : Data.getInstanceQueue()) {
                        entities.add(new Variable(instance));
                    }

                    return new Variable(entities);
                }),

                // &&&&&&&&&&&&&&&&&&&&&&&&&&& list eachCreature ()
                new Command(TokenConstants.EACH_CREATURE.getValue(), 0, (self, parameters) -> {
                    if (Data.getPublicInstanceList() == null) {
                        return Variable.emptyList();
                    }

                    List<Variable> creatures = new ArrayList<>(Data.getPublicInstanceList().size()/2);
                    for (Instance instance : Data.getInstanceQueue()) {
                        Container container = Data.getContainer(instance.getId());
                        if (container != null && container.getType() == DataType.CREATURE) {
                            creatures.add(new Variable(instance));
                        }
                    }
                    return new Variable(creatures);
                })
        );
    }

}
