package engine.graphics.gui;

import engine.graphics.objects.GraphicalObject;
import engine.graphics.objects.TextObject;
import engine.graphics.objects.generators.MeshGenerator;
import load.TextureLoader;

public class BaseHUD implements HUDInterface {

	private GraphicalObject[] objects;

	public BaseHUD() {
		initializeTestObjects();
	}

	private void initializeTestObjects() {
		objects = new GraphicalObject[2];

		objects[0] = new TextObject("Test");
		//objects[0].setPosition(-1,-1,0);
		//objects[0].setScale(0.1,0.1,1);
		//objects[0].getMesh().randomizeTextureCoordinates();

		objects[1] = new GraphicalObject(MeshGenerator.createQuad());
		objects[1].setTexture(TextureLoader.loadTexture("data/mods/vanilla/assets/textures/trollface.png"));
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	@Override
	public GraphicalObject[] getGraphicalObjects() {
		return objects;
	}
}
