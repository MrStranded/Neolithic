package engine.logic.topology;

import engine.data.entities.Instance;
import engine.data.planetary.Tile;
import engine.math.numericalObjects.Vector3;

public class Pathfinding {

    public static Tile moveTowardsTile(Instance instance, Tile to, int steps) {
        Tile from = instance.getPosition();
        if (from == null || to == null) { return from; }

        Tile currentPosition = from;
        double currentDistance = getHeuristic(from, to) * 2.0; // times two to improve attractiveness of neighbours

        if (steps > 0) {
            //System.out.println("doing steps: " + instance + " from " + from + " to " + to + ": " + currentDistance);
            for (int step = 0; step < steps; step++) {

                for (Tile neighbour : Neighbour.getNeighbours(currentPosition)) {
                    // is neighbour accessible ? -> still has to be implemented!
                    double newDistance = getHeuristic(neighbour, to);
                    //System.out.println(currentPosition + " to " + neighbour + ": " + newDistance);

                    if (newDistance < currentDistance) {
                        currentDistance = newDistance;
                        currentPosition = neighbour;
                    }
                }

                //System.out.println("next tile: " + currentPosition);
            }
        }

        return currentPosition;
    }

    public static double getHeuristic(Tile from, Tile to) {
        if (from == null || to == null) { return 0; }

        return getSquaredDistance(from, to) + 0*(to.getHeight() - from.getHeight());
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
