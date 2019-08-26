package engine.graphics.gui.statistics;

import constants.GameConstants;

public class StatisticsData {

    private static int[] internalCounts = new int[GameConstants.MAX_CONTAINERS];
    private static int[] counts = new int[GameConstants.MAX_CONTAINERS];

    private static int[] lowest = new int[GameConstants.MAX_ATTRIBUTES];
    private static int[] highest = new int[GameConstants.MAX_ATTRIBUTES];

    public static void clear() {
        counts = internalCounts;
        internalCounts = new int[GameConstants.MAX_CONTAINERS];
    }

    public static void add(int id) {
        if (id >= 0 && id < GameConstants.MAX_CONTAINERS) {
            internalCounts[id]++;
        }
    }

    public static int getCount(int id) {
        if (id >= 0 && id < GameConstants.MAX_CONTAINERS) {
            return counts[id];
        }
        return 0;
    }

    public static void registerAttributeValue(int id, int value) {
        if (id >= 0 && id < GameConstants.MAX_ATTRIBUTES) {
            if (value < lowest[id]) {
                lowest[id] = value;
            }
            if (value > highest[id]) {
                highest[id] = value;
            }
        }
    }

    public static int getLowest(int id) {
        if (id >= 0 && id < GameConstants.MAX_ATTRIBUTES) {
            return lowest[id];
        }
        return 0;
    }
    public static int getHighest(int id) {
        if (id >= 0 && id < GameConstants.MAX_ATTRIBUTES) {
            return highest[id];
        }
        return 1;
    }

}
