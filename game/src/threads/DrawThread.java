package threads;

import gui.graphics.GraphicsHandler;

/**
 * Created by Michael on 11.07.2017.
 *
 * This class draws the new window every few miliseconds.
 */
public class DrawThread extends Thread {

	private int sleepTime;

	public DrawThread(int sleepTime) {
		this.sleepTime = sleepTime;

		setDaemon(true);
	}

	public void run() {
		GraphicsHandler.init();

		while (GraphicsHandler.draw()) {

			try {
				sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		GraphicsHandler.tearDown();
	}

}
