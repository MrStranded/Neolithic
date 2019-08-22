package engine.data.scripts;

import engine.data.entities.Instance;
import engine.data.variables.Variable;

/**
 * With this class we can remember which scripts we want to run how.
 * ScriptRuns are added from outside of the logic loop. This is why they have to be treated carefully, such as not to
 * run into concurrency issues.
 * ScriptRuns are buffered in the Data class.
 */
public class ScriptRun {

    private Instance target;
    private String scriptTextId;
    private Variable[] parameters;

    public ScriptRun(Instance target, String scriptTextId, Variable[] parameters) {
        this.target = target;
        this.scriptTextId = scriptTextId;
        this.parameters = parameters;
    }

    public Variable run() {
        return target.run(scriptTextId, parameters);
    }

}
