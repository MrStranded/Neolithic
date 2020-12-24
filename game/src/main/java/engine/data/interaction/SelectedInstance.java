package engine.data.interaction;

import engine.data.variables.Variable;

import java.util.HashMap;
import java.util.Map;

public class SelectedInstance {

    // ----------------------------------------------------------------------------------------------- singleton
    private static SelectedInstance instance = new SelectedInstance();
    private SelectedInstance() {}

    public static SelectedInstance instance() {
        return instance;
    }

    // ----------------------------------------------------------------------------------------------- declaration
    private Map<String, Variable> values = new HashMap<>();

    private final static String DRIVE_CONDITION = "dc_";
    private final static String DRIVE_WEIGHT = "dc_";

    private final static String TASK_NAME = "t_n";
    private final static String TASK_RESULT = "t_r";

    // ----------------------------------------------------------------------------------------------- drives
    public void putDrive(String textId, Variable condition, Variable weight) {
        values.put(DRIVE_CONDITION + textId, condition != null ? condition : new Variable());
        values.put(DRIVE_WEIGHT + textId, weight != null ? weight : new Variable());
    }

    public Variable getDriveCondition(String textId) {
        return values.getOrDefault(DRIVE_CONDITION + textId, new Variable());
    }
    public Variable getDriveWeight(String textId) {
        return values.getOrDefault(DRIVE_WEIGHT + textId, new Variable());
    }

    // ----------------------------------------------------------------------------------------------- current task
    public void putCurrentTask(String taskName, Variable result) {
        values.put(TASK_NAME, new Variable(taskName));
        values.put(TASK_RESULT, result);
    }

    public String getCurrentTaskName() {
        return values.getOrDefault(TASK_NAME, new Variable("-")).getString();
    }
    public Variable getCurrentTaskResult() {
        return values.getOrDefault(TASK_RESULT, new Variable());
    }

    // ----------------------------------------------------------------------------------------------- clear
    public void clear() {
        values.clear();
    }
}
