package engine.graphics.gui;

import engine.graphics.objects.GraphicalObject;
import engine.graphics.objects.TextObject;
import engine.graphics.objects.generators.MeshGenerator;
import engine.graphics.objects.textures.FontTexture;
import load.TextureLoader;

import java.awt.*;

public class BaseHUD implements HUDInterface {

	private GraphicalObject[] objects;

	public BaseHUD() {
		initializeTestObjects();
	}

	private void initializeTestObjects() {
		objects = new GraphicalObject[2];

		FontTexture fontTexture = null;
		try {
			fontTexture = new FontTexture(new Font("Arial", Font.PLAIN, 20), "ISO-8859-1"); // UTF-8 , US-ASCII , ISO-8859-1
		} catch (Exception e) {
			e.printStackTrace();
		}
		objects[0] = new TextObject("Test", fontTexture);
		//objects[0].setPosition(-1,-1,0);
		double s = 1d/40d;
		objects[0].setScale(s,s,s);
		//objects[0].getMesh().randomizeTextureCoordinates();

		objects[1] = new GraphicalObject(MeshGenerator.createQuad());
		objects[1].setTexture(TextureLoader.loadTexture("data/mods/vanilla/assets/textures/trollface.png"));
		//objects[1].setTexture(fontTexture.getTexture());
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	@Override
	public GraphicalObject[] getGraphicalObjects() {
		return objects;
	}
}
