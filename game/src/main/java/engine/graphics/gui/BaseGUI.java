package engine.graphics.gui;

import engine.graphics.objects.gui.GUIObject;
import engine.graphics.objects.gui.TextObject;
import engine.graphics.objects.generators.MeshGenerator;
import engine.graphics.objects.textures.FontTexture;
import load.TextureLoader;

import java.awt.*;

public class BaseGUI implements GUIInterface {

	private GUIObject[] objects;

	public BaseGUI() {
		initializeTestObjects();
	}

	private void initializeTestObjects() {
		objects = new GUIObject[2];

		FontTexture fontTexture = null;
		try {
			fontTexture = new FontTexture(new Font("Arial", Font.PLAIN, 50), "ISO-8859-1"); // UTF-8 , UTF-16 , US-ASCII , ISO-8859-1 (the utf charsets don't work)
		} catch (Exception e) {
			e.printStackTrace();
		}
		objects[1] = new TextObject("Neolithic - A Stone Age Game", fontTexture);
		objects[1].setSize(600,30);
		objects[1].setPosition(0,0,0.5d);
		objects[1].setPosition(0,0,-0.25d);
		objects[1].setRelativeScreenPositionX(RelativeScreenPosition.CENTER);

		objects[0] = new GUIObject(MeshGenerator.createQuad());
		objects[0].getMesh().getMaterial().setTexture(TextureLoader.loadTexture("data/mods/vanilla/assets/textures/trollface.png"));
		objects[0].setSize(80,80);
		//objects[0].setLocation(0,100);
		objects[0].setPosition(0,0,-0.5d);
		//objects[0].getMesh().setColor(0.5f, 0.5f, 0.5f, 0.5f);
		//objects[0].setLocation(200, 400);
		//objects[0].setRelativeScreenPosition(RelativeScreenPosition.RIGHT, RelativeScreenPosition.BOTTOM);
		objects[0].setRelativeScreenPosition(RelativeScreenPosition.LEFT, RelativeScreenPosition.TOP);
	}

	// ###################################################################################
	// ################################ Clean Up #########################################
	// ###################################################################################

	public void cleanUp() {
		for (GUIObject object : objects) {
			if (object != null) {
				object.cleanUp();
			}
		}
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	@Override
	public GUIObject[] getHUDObjects() {
		return objects;
	}
}
