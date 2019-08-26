package engine.graphics.gui.statistics;

import constants.GameConstants;

public class StatisticsData {

    private static int[] internalCounts = new int[GameConstants.MAX_CONTAINERS];
    private static int[] counts = new int[GameConstants.MAX_CONTAINERS];

    private static int[] countAttributes = new int[GameConstants.MAX_ATTRIBUTES];
    private static int[] sums = new int[GameConstants.MAX_ATTRIBUTES];
    private static int[] lowest = new int[GameConstants.MAX_ATTRIBUTES];
    private static int[] highest = new int[GameConstants.MAX_ATTRIBUTES];

    public static void clear() {
        counts = internalCounts;
        internalCounts = new int[GameConstants.MAX_CONTAINERS];
        for (int i = 0; i < GameConstants.MAX_ATTRIBUTES; i++) {
            countAttributes[i] = 0;
            sums[i] = 0;
        }
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
            sums[id] += value;
            countAttributes[id]++;
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

    public static double getAverage(int id) {
        if (id >= 0 && id < GameConstants.MAX_ATTRIBUTES) {
            if (countAttributes[id] > 0) {
                return (double) sums[id] / (double) countAttributes[id];
            }
        }
        return 0;
    }

}
