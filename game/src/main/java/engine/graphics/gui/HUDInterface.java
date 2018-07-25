package engine.graphics.gui;

import engine.graphics.objects.GraphicalObject;

public interface HUDInterface {

	GraphicalObject[] getGraphicalObjects();

	default void cleanUp() {
		GraphicalObject[] objects = getGraphicalObjects();
		for (GraphicalObject object : objects) {
			object.cleanUp();
		}
	}

}
