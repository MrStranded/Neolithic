package engine.data.behaviour;

import engine.data.entities.Instance;
import engine.data.scripts.Script;

public class Occupation {

    int completeDuration;
    int remainingDuration;
    Script callBackScript;

    public Occupation(int duration, Script callBackScript) {
        completeDuration = duration;
        remainingDuration = duration;
        this.callBackScript = callBackScript;
    }

    public boolean isFinished() {
        return remainingDuration <= 0;
    }

    public void tick() {
        remainingDuration--;
    }

    public void callBack(Instance instance) {
        if (instance != null && callBackScript != null) {
            instance.run(callBackScript, null);
        }
    }

}
