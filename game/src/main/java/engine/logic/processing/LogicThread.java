package engine.logic.processing;

import constants.ScriptConstants;
import engine.data.Data;
import engine.data.entities.Instance;
import engine.graphics.gui.window.Window;
import engine.parser.scripts.nodes.ScriptCallNode;

import java.util.Queue;

public class LogicThread extends Thread {

	private Window window;
	private long t;
	private long timePerCreature = 1;

	public LogicThread(Window window) {
		this.window = window;
	}

	@Override
	public void run() {
		t = System.currentTimeMillis();
		while (!window.isClosed()) {
			long currentTime = System.currentTimeMillis();

			if (currentTime - t >= timePerCreature) {
				Instance creature = Data.getNextCreature();

				if (creature != null) {
					creature.runScript(ScriptConstants.EVENT_TICK, null);

					Data.addCreatureToQueue(creature);
				}

				t = System.currentTimeMillis();
			} else {
				try {
					sleep(timePerCreature - (currentTime - t));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
