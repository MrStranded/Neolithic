package engine.logic.processing;

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
		while (!window.isClosed()) {
			Instance creature = Data.getNextCreature();

			if (creature != null) {
				creature.runScript(ScriptConstants.EVENT_TICK, null);

				Data.addCreatureToQueue(creature);
			}
		}
	}

}
