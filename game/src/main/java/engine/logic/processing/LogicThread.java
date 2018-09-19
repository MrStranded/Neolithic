package engine.logic.processing;

import constants.GameConstants;
import constants.ScriptConstants;
import engine.data.Data;
import engine.data.entities.Instance;
import engine.graphics.gui.window.Window;
import engine.parser.scripts.nodes.ScriptCallNode;

import java.util.Queue;

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
				Instance creature = Data.getNextCreature();

				if (creature != null && !creature.isSlatedForRemoval()) {
					creature.tick();

					Data.addCreatureToQueue(creature);
				}

				t = System.currentTimeMillis();
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
