package engine.data.behaviour;

import engine.data.scripts.Script;
import engine.data.entities.Instance;

public class Occupation {

    int completeDuration = 0;
    int remainingDuration = 0;
    Script callBackScript = null;

    public Occupation(int duration, Script callBackScript) {
        completeDuration = duration;
        this.callBackScript = callBackScript;
    }

    public boolean isFinished() {
        return remainingDuration >= completeDuration;
    }

    public void tick() {
        remainingDuration++;
    }

    public void callBack(Instance instance) {
        if (instance != null && callBackScript != null) {
            instance.run(callBackScript, null);
        }
    }

}
