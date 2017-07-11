package threads;

import gui.Window;

/**
 * Created by Michael on 11.07.2017.
 *
 * This class draws the new window every few miliseconds.
 */
public class DrawThread extends Thread {

	private Window window;
	private int sleepTime;

	public DrawThread(Window window, int sleepTime) {
		this.window = window;
		this.sleepTime = sleepTime;
	}

	public void run() {
		while (true) {
			window.redraw();

			try {
				sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
