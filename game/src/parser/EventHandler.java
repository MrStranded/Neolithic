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
			System.out.println("called event: "+event);
			for (Entity entity : Data.getProtoEntities()) {
				System.out.println("entity: "+entity.getTextID());
				if (event.equals(entity.getTextID())) {
					System.out.println("found entity");
					ScriptExecuter.executeScriptBlocks(entity.getScriptBlocks(),entity);
					return;
				}
			}
		}
	}

}
