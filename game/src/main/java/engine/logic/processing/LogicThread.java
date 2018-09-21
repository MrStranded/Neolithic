package engine.logic.processing;

import constants.GameConstants;
import engine.data.Data;
import engine.data.entities.Instance;
import engine.data.variables.DataType;
import engine.graphics.gui.window.Window;

public class LogicThread extends Thread {

	private Window window;

	public LogicThread(Window window) {
		this.window = window;
	}

	@Override
	public void run() {
		long t = System.currentTimeMillis();
		while (!window.isClosed()) {
			long currentTime = System.currentTimeMillis();

			if (currentTime - t >= GameConstants.TICK_TIME_PER_CREATURE) {
				Instance instance = Data.getNextInstance();
				boolean isCreature = Data.getContainer(instance.getId()).getType() == DataType.CREATURE;

				if (instance != null && !instance.isSlatedForRemoval()) {
					if (isCreature) {
						instance.tick();
					}

					Data.addInstanceToQueue(instance);
				}

				if (isCreature) {
					t = System.currentTimeMillis();
				}
			} else {
				try {
					sleep(GameConstants.TICK_TIME_PER_CREATURE - (currentTime - t));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
