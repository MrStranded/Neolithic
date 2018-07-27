package engine.graphics.gui;

import engine.graphics.objects.HUDObject;

public interface HUDInterface {

	HUDObject[] getHUDObjects();

	default void cleanUp() {
		HUDObject[] objects = getHUDObjects();
		for (HUDObject object : objects) {
			object.cleanUp();
		}
	}

}
