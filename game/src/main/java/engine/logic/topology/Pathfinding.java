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
     * This private class is used to remember the tiles with their heuristic and g values.
     * g denotes how many steps have already been taken to reach this tile
     */
    private static class TileHeuristic implements Comparable<TileHeuristic> {
        public Tile tile;
        public double h, g;

        TileHeuristic(Tile tile, double h, double g) {
            this.tile = tile;
            this.h = h;
            this.g = g;
        }

        @Override
        public int compareTo(TileHeuristic o) {
            return (int) ((h+g*0.001) - (o.h+o.g*0.001));
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
    public static Tile moveTowardsTile(Instance instance, Tile to, int steps) {
        Tile from = instance.getPosition();
        if (from == null || to == null) { return from; }

        PriorityQueue<TileHeuristic> openList = new PriorityQueue<>();
        HashSet<Tile> closedList = new HashSet<>();

        openList.add(new TileHeuristic(from, getHeuristic(from, to), 0));
        Tile finalTile = from;

        while (true) {
            TileHeuristic currentTile = openList.poll();
            if (currentTile == null) { break; } // nowhere to go
            closedList.add(currentTile.tile);

            if (currentTile.tile == to || currentTile.g >= steps) {
                finalTile = currentTile.tile;
                break;
            }

            for (Tile neighbour : Neighbour.getNeighbours(currentTile.tile)) {
                if (!closedList.contains(neighbour)) {
                    if (instance.canGo(currentTile.tile, neighbour)) {
                        double h = getHeuristic(neighbour, to);
                        openList.add(new TileHeuristic(neighbour, h, currentTile.g + 1));
                    }
                }
            }
        }

        return finalTile;
    }

    public static double getHeuristic(Tile from, Tile to) {
        if (from == null || to == null) { return 0; }

        return getSquaredDistance(from, to) * Data.getPlanet().getSize() * TopologyConstants.HEURISTIC_MULTIPLIER; // this brings the distance to a range of about 0.25 to 25
    }

    public static double getSquaredDistance(Tile from, Tile to) {
        if (from == null || to == null) { return 0; }
        if (from.getHeight() == 0 || to.getHeight() == 0) { return 0; }

        Vector3 normalizedMidFrom = /*from.getTileMesh().getMid();//*/new Vector3(from.getTileMesh().getMid()).times(1d / (double) from.getHeight());
        Vector3 normalizedMidTo = /*to.getTileMesh().getMid();//*/new Vector3(to.getTileMesh().getMid()).times(1d / (double) to.getHeight());

        return    ( normalizedMidTo.getX()-normalizedMidFrom.getX() )*( normalizedMidTo.getX()-normalizedMidFrom.getX() )
                + ( normalizedMidTo.getY()-normalizedMidFrom.getY() )*( normalizedMidTo.getY()-normalizedMidFrom.getY() )
                + ( normalizedMidTo.getZ()-normalizedMidFrom.getZ() )*( normalizedMidTo.getZ()-normalizedMidFrom.getZ() );
    }

}
