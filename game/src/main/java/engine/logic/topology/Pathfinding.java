package engine.logic.topology;

import constants.TopologyConstants;
import engine.data.Data;
import engine.data.entities.Instance;
import engine.data.planetary.Tile;
import engine.math.numericalObjects.Vector3;

public class Pathfinding {

    public static Tile moveTowardsTile(Instance instance, Tile to, int steps) {
        Tile from = instance.getPosition();
        if (from == null || to == null) { return from; }

        Tile currentPosition = from;
        double currentDistance = getHeuristic(from, to) * TopologyConstants.CURRENT_POSITION_HEURISTIC_MULTIPLIER; // times a factor to improve attractiveness of neighbours

        if (steps > 0) {
            for (int step = 0; step < steps; step++) {
                for (Tile neighbour : Neighbour.getNeighbours(currentPosition)) {
                    if (instance.canGo(currentPosition, neighbour)) {
                        double newDistance = getHeuristic(neighbour, to);
                        //System.out.println(currentPosition + " to " + neighbour + ": " + newDistance);

                        if (newDistance < currentDistance) {
                            currentDistance = newDistance;
                            currentPosition = neighbour;
                        }
                    }
                }
            }
        }

        return currentPosition;
    }

    public static double getHeuristic(Tile from, Tile to) {
        if (from == null || to == null) { return 0; }

        return getSquaredDistance(from, to) * Data.getPlanet().getSize() * TopologyConstants.HEURISTIC_MULTIPLIER; // this brings the distance to a range of 0.25 to 25
    }

    public static double getSquaredDistance(Tile from, Tile to) {
        if (from == null || to == null) { return 0; }
        if (from.getHeight() == 0 || to.getHeight() == 0) { return 0; }

        Vector3 normalizedMidFrom = new Vector3(from.getTileMesh().getMid()).times(1d / (double) from.getHeight());
        Vector3 normalizedMidTo = new Vector3(to.getTileMesh().getMid()).times(1d / (double) to.getHeight());

        return    ( normalizedMidTo.getX()-normalizedMidFrom.getX() )*( normalizedMidTo.getX()-normalizedMidFrom.getX() )
                + ( normalizedMidTo.getY()-normalizedMidFrom.getY() )*( normalizedMidTo.getY()-normalizedMidFrom.getY() )
                + ( normalizedMidTo.getZ()-normalizedMidFrom.getZ() )*( normalizedMidTo.getZ()-normalizedMidFrom.getZ() );
    }

}
