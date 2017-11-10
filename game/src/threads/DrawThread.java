package threads;

import gui.WindowInterface;

/**
 * Created by Michael on 11.07.2017.
 *
 * This class draws the new window every few miliseconds.
 */
public class DrawThread extends Thread {

	private int sleepTime;
	private WindowInterface window;

	public DrawThread(WindowInterface window, int sleepTime) {
		this.window = window;
		this.sleepTime = sleepTime;

		setDaemon(true);
	}

	public void run() {
		window.init();

		while (window.draw()) {

			try {
				sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		window.close();
	}

}
