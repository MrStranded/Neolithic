package threads;

import environment.world.Planet;
import environment.meteorology.Rain;

/**
 * Created by Michael on 14.07.2017.
 *
 * This class creates rain, works the clouds and lets it rain.
 */
public class MeteorologyThread extends Thread {

	private Planet planet;
	private int sleepTime;

	public MeteorologyThread(Planet planet, int sleepTime) {
		this.planet = planet;
		this.sleepTime = sleepTime;
	}

	public void run() {
		while (true) {
			Rain.letItRain(planet);

			try {
				sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
