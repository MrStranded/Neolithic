package engine.graphics.gui.statistics;

import constants.GameConstants;

public class StatisticsData {

    private static int[] internalCounts = new int[GameConstants.MAX_CONTAINERS];
    private static int[] counts = new int[GameConstants.MAX_CONTAINERS];

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

}
