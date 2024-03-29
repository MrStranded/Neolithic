package engine.threads;

import constants.GameConstants;
import engine.Engine;
import engine.data.Data;
import engine.data.entities.Instance;
import engine.data.options.GameOptions;
import engine.graphics.gui.GuiData;
import engine.parser.utils.Logger;

public class LogicThread extends Thread {

	public LogicThread() {
		setDaemon(true);
	}

	@Override
	public void run() {
		long t = System.currentTimeMillis();

		while (! GuiData.getRenderWindow().isClosed()) {
			long currentTime = System.currentTimeMillis();

			if (currentTime - t >= GameConstants.TIME_BETWEEN_TICK_LOADS) {

				// reloading scripts
				if (GameOptions.reloadScripts) {
					Engine.reloadScripts();
					GameOptions.reloadScripts = false;
				}

				// running pending externally called scripts
				if (! Data.getScriptRuns().isEmpty()) {
					Data.getScriptRuns().poll().run();
				}

				// calculating instances load
				if (GameOptions.runTicks) {
					for (int i = 0; i < GameConstants.INSTANCES_PER_TICK; i++) {
						Instance instance = Data.getNextInstance();

						// special handling the main instance -> updating current public instance list
						if (instance == Data.getMainInstance()) {
							if (GameOptions.plotEntities) { GuiData.getStatisticsWindow().tick(); }
							GuiData.getHud().tick();

							if (GameOptions.stopAtNextTick) {
								GameOptions.stopAtNextTick = false;
								GameOptions.runTicks = false;
							}
						}

						// treating the instance itself
						if (instance != null) {
							instance.tick();

							if (! instance.isSlatedForRemoval()) {
								if (GameOptions.plotEntities) { GuiData.getStatisticsWindow().register(instance); }

								Data.addInstanceToQueue(instance);
							}
						}
					}
				}

				if (GameOptions.printPerformance) {
					long dt = (System.currentTimeMillis() - t);
					if (dt > 100) {
						Logger.raw("Logic Loop took: " + dt);
					}
				}
				t = System.currentTimeMillis();
			} else {
				try {
					sleep((GameConstants.TIME_BETWEEN_TICK_LOADS - (currentTime - t)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
