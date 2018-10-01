package engine.logic.processing;

import constants.GameConstants;
import engine.data.Data;
import engine.data.entities.Instance;
import engine.graphics.gui.window.Window;

public class LogicThread extends Thread {

	private Window window;

	public LogicThread(Window window) {
		this.window = window;
		setDaemon(true);
	}

	@Override
	public void run() {
		long t = System.currentTimeMillis();
		while (!window.isClosed()) {
			long currentTime = System.currentTimeMillis();

			if (currentTime - t >= GameConstants.TICK_TIME_PER_INSTANCE) {
				Instance instance = Data.getNextInstance();

				if (instance != null) {
					if (!instance.isSlatedForRemoval()) {
						instance.tick();

						Data.addInstanceToQueue(instance);
					}

					t = System.currentTimeMillis();
				}
			} else {
				try {
					sleep(GameConstants.TICK_TIME_PER_INSTANCE - (currentTime - t));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
