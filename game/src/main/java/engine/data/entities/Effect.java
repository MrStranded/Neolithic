package engine.data.entities;

import constants.ScriptConstants;
import engine.data.variables.Variable;

public class Effect extends Instance {

    private int remainingTicks = -1;

    public Effect(int id) {
        super(id);
    }

    // ###################################################################################
    // ################################ Logic ############################################
    // ###################################################################################

    public void tick(Instance carrier) {
        if (remainingTicks == -1) {
            run(ScriptConstants.EVENT_TICK, new Variable[]{new Variable(carrier)});
        } else {
            if (remainingTicks > 0) { remainingTicks--; }
        }
    }

    public boolean shouldBeRemoved(Instance carrier) {
        if (remainingTicks == -1) {
            return ! run(ScriptConstants.EVENT_REMOVE_CONDITION, new Variable[]{new Variable(carrier)}).isNull();
        } else {
            return (remainingTicks == 0);
        }
    }

    public void callBack(Instance carrier) {
        run(ScriptConstants.EVENT_FINALLY, new Variable[]{new Variable(carrier)});
    }

    // ###################################################################################
    // ################################ Getters and Setters ##############################
    // ###################################################################################

    public void setRemainingTicks(int remainingTicks) {
        this.remainingTicks = remainingTicks;
    }

    public int getRemainingTicks() {
        return remainingTicks;
    }

    // ###################################################################################
    // ################################ Debugging ########################################
    // ###################################################################################

    public String toString() {
        return "Effect (id = " + id + (getName() != null ? " ,name = " + getName() : "") + ")";
    }
}
