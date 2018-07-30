package engine.graphics.gui;

import engine.graphics.objects.GUIObject;
import engine.graphics.objects.TextObject;
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
		objects[0] = new TextObject("Test", fontTexture);
		objects[0].setSize(200,200);
		objects[0].setRelativeScreenPositionY(RelativeScreenPosition.CENTER);

		objects[1] = new GUIObject(MeshGenerator.createQuad());
		objects[1].setTexture(TextureLoader.loadTexture("data/mods/vanilla/assets/textures/trollface.png"));
		objects[1].setSize(200,200);
		//objects[1].setLocation(200, 400);
		objects[1].setRelativeScreenPosition(RelativeScreenPosition.LEFT, RelativeScreenPosition.CENTER);
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	@Override
	public GUIObject[] getHUDObjects() {
		return objects;
	}
}
