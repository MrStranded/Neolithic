package engine.threads;

import constants.GameConstants;
import engine.data.Data;
import engine.data.entities.Instance;
import engine.data.options.GameOptions;
import engine.graphics.gui.window.Window;

import java.util.ArrayList;
import java.util.List;

public class LogicThread extends Thread {

	private Window window;

	public LogicThread(Window window) {
		this.window = window;
		setDaemon(true);
	}

	@Override
	public void run() {
		int lastSize = 100;
		// each round (mainInstance to mainInstance) we construct the current public instance list from anew
		List<Instance> instanceList = new ArrayList<>(lastSize);

		long t = System.currentTimeMillis();
		while (!window.isClosed()) {
			long currentTime = System.currentTimeMillis();

			if (currentTime - t >= GameConstants.TIME_BETWEEN_TICK_LOADS) {

				// running pending externally called scripts
				if (!Data.getScriptRuns().isEmpty()) {
					Data.getScriptRuns().poll().run();
				}

				// calculating instances load
				if (GameOptions.runTicks) {
					for (int i = 0; i < GameConstants.INSTANCES_PER_TICK; i++) {
						Instance instance = Data.getNextInstance();

						// special handling the main instance -> updating current public instance list
						if (instance == Data.getMainInstance()) {
							Data.setPublicInstanceList(instanceList);
							instanceList = new ArrayList<>(lastSize);
							lastSize = 0;
						}

						// treating the instance itself
						if (instance != null) {
							if (!instance.isSlatedForRemoval()) {
								instance.tick();

								Data.addInstanceToQueue(instance);
								instanceList.add(instance);
								lastSize++;
							}
						}
					}
				}

				if (GameOptions.printPerformance) {
					long dt = (System.currentTimeMillis() - t);
					if (dt > 100) {
						System.out.println("Logic Loop took: " + dt);
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
