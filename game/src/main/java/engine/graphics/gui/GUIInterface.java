package engine.graphics.gui;

import engine.graphics.objects.gui.GUIObject;

public interface GUIInterface {

	GUIObject[] getHUDObjects();

	default void cleanUp() {
		GUIObject[] objects = getHUDObjects();
		for (GUIObject object : objects) {
			object.cleanUp();
		}
	}

}
