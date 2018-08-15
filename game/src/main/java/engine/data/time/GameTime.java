package engine.data.time;

public class GameTime {

	private long milliseconds = 0;

	public GameTime(long milliseconds) {
		this.milliseconds = milliseconds;
	}

	public GameTime(GameTime other) {
		milliseconds = other.milliseconds;
	}

	public void add(long milliseconds) {
		this.milliseconds += milliseconds;
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public long getMilliseconds() {
		return milliseconds;
	}
	public void setMilliseconds(long milliseconds) {
		this.milliseconds = milliseconds;
	}
}
