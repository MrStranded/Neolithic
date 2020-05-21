package engine;

import engine.data.options.GameOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TimedTask {
    private String description;
    private TimedAction action;
    private int minTimeForOutput;

    public TimedTask(String description, TimedAction action) {
        this(description, action, 0);
    }

    public TimedTask(String description, TimedAction action, int minTimeForOutput) {
        this.description = description;
        this.action = action;
        this.minTimeForOutput = minTimeForOutput;
    }

    public static List<TimedTask> listOf(TimedTask... tasks) {
        if (tasks == null) { return Collections.emptyList(); }

        return Arrays.asList(tasks);
    }

    public void execute() {
        long start = System.currentTimeMillis();

        action.execute();

        if (GameOptions.printPerformance) {
            long dt = (System.currentTimeMillis() - start);
            if (dt >= minTimeForOutput) {
                System.out.println(description + " took: " + dt);
            }
        }
    }

    @FunctionalInterface
    public interface TimedAction {
        void execute();
    }
}
