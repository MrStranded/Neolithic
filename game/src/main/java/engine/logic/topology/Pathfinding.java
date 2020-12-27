package engine.logic.topology;

import constants.TopologyConstants;
import engine.data.Data;
import engine.data.entities.Instance;
import engine.data.planetary.Tile;
import engine.math.numericalObjects.Vector3;

import java.util.HashSet;
import java.util.PriorityQueue;

public class Pathfinding {

    /**
     * This private class is used to remember the tiles with their heuristic and steps value.
     * steps denotes how many steps have already been taken to reach this tile
     */
    private static class TileHeuristic implements Comparable<TileHeuristic> {
        public Tile tile;
        public double heuristic, steps;

        private static final double FACTOR = 0.1;

        TileHeuristic(Tile tile, double heuristic, double steps) {
            this.tile = tile;
            this.heuristic = heuristic;
            this.steps = steps;
        }

        @Override
        public int compareTo(TileHeuristic o) {
//            System.out.println("this = " + heuristic + " , " + steps + "   " + tile);
//            System.out.println("other = " + o.heuristic + " , " + o.steps + "   " + o.tile);
            return (int) Math.signum((heuristic + steps*FACTOR) - (o.heuristic + o.steps*FACTOR));
        }
    }

    /**
     * Tries to move towards the specified tile for the given number of steps.
     * This method implements an A* algorithm.
     * @param instance to move
     * @param to goal tile
     * @param steps number of allowed steps
     * @return tile closest to goal tile with given steps
     */
    public static Tile moveTowardsTile(Instance instance, final Tile to, int steps, int viewingDistance) {
        Tile from = instance.getPosition();
        if (from == null || to == null) { return from; }

        PriorityQueue<TileHeuristic> openList = new PriorityQueue<>();
        HashSet<Tile> closedList = new HashSet<>();

        TileHeuristic bestTile = new TileHeuristic(from, getHeuristic(from, to), 0);
        openList.add(bestTile);

        while (true) {
            TileHeuristic currentTile = openList.poll();
            if (currentTile == null) { break; } // nowhere to go
            closedList.add(currentTile.tile);

            if (currentTile.steps <= steps
                    && (currentTile.tile == to || currentTile.compareTo(bestTile) > 0)) {
                bestTile = currentTile;
                break;
            }

            if (currentTile.steps < viewingDistance) {
                for (Tile neighbour : Neighbour.getNeighbours(currentTile.tile)) {
                    if (! closedList.contains(neighbour) && instance.canGo(currentTile.tile, neighbour)) {
                        double heuristic = getHeuristic(neighbour, to);
                        openList.add(new TileHeuristic(neighbour, heuristic, currentTile.steps + 1));
                    }
                }
            }
        }

        return bestTile.tile;
    }

    public static double getHeuristic(Tile from, Tile to) {
        if (from == null || to == null) { return 0; }

        return getSquaredDistance(from, to) * Data.getPlanet().getSize() * TopologyConstants.HEURISTIC_MULTIPLIER; // this brings the distance to a range of about 0.25 to 25
    }

    public static double getSquaredDistance(Tile from, Tile to) {
        if (from == null || to == null) { return 0; }

        double fromHeight = TopologyConstants.PLANET_MINIMUM_HEIGHT + from.getHeight() / TopologyConstants.PLANET_MAXIMUM_HEIGHT;
        double   toHeight = TopologyConstants.PLANET_MINIMUM_HEIGHT +   to.getHeight() / TopologyConstants.PLANET_MAXIMUM_HEIGHT;

        Vector3 normalizedMidFrom = new Vector3(from.getTileMesh().getMid()).times(1d / fromHeight);
        Vector3   normalizedMidTo = new Vector3(  to.getTileMesh().getMid()).times(1d /   toHeight);

        return    ( normalizedMidTo.getX()-normalizedMidFrom.getX() )*( normalizedMidTo.getX()-normalizedMidFrom.getX() )
                + ( normalizedMidTo.getY()-normalizedMidFrom.getY() )*( normalizedMidTo.getY()-normalizedMidFrom.getY() )
                + ( normalizedMidTo.getZ()-normalizedMidFrom.getZ() )*( normalizedMidTo.getZ()-normalizedMidFrom.getZ() );
    }

}
