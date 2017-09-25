package parser;

import data.Data;
import environment.world.Entity;

/**
 * this class should handle all kinds of event calls and see that the appropriate code is executed
 *
 * Created by Michael on 12.09.2017.
 */
public class EventHandler {

	/**
	 * Call an event.
	 * Either looks in the scriptBlocks of the parent or,
	 * if it is null looks at the entities in the Data list.
	 */
	public static void call(String event, Entity parent) {
		if (event == null) return;

		if (parent == null) {
		}
	}

}
