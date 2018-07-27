package engine.graphics.gui;

import engine.graphics.objects.GraphicalObject;
import engine.graphics.objects.TextObject;

public class BaseHUD implements HUDInterface {

	private GraphicalObject[] objects;

	public BaseHUD() {
		initializeTestObjects();
	}

	private void initializeTestObjects() {
		objects = new GraphicalObject[1];

		objects[0] = new TextObject("Test");
		//objects[0].setPosition(-1,-1,0);
		//objects[0].setScale(0.1,0.1,1);
		//objects[0].getMesh().randomizeTextureCoordinates();
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	@Override
	public GraphicalObject[] getGraphicalObjects() {
		return objects;
	}
}
