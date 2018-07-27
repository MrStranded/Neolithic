package engine.graphics.gui;

import engine.graphics.objects.HUDObject;
import engine.graphics.objects.TextObject;
import engine.graphics.objects.generators.MeshGenerator;
import engine.graphics.objects.textures.FontTexture;
import load.TextureLoader;

import java.awt.*;

public class BaseHUD implements HUDInterface {

	private HUDObject[] objects;

	public BaseHUD() {
		initializeTestObjects();
	}

	private void initializeTestObjects() {
		objects = new HUDObject[2];

		FontTexture fontTexture = null;
		try {
			fontTexture = new FontTexture(new Font("Arial", Font.PLAIN, 50), "ISO-8859-1"); // UTF-8 , UTF-16 , US-ASCII , ISO-8859-1 (the utf charsets don't work)
		} catch (Exception e) {
			e.printStackTrace();
		}
		objects[0] = new TextObject("Test", fontTexture);
		//objects[0].setPosition(-1,-1,0);
		//double s = 1d/100d;
		//objects[0].setScale(s,s,s);
		//objects[0].getMesh().randomizeTextureCoordinates();

		objects[1] = new HUDObject(MeshGenerator.createQuad());
		objects[1].setTexture(TextureLoader.loadTexture("data/mods/vanilla/assets/textures/trollface.png"));
		//objects[1].setTexture(fontTexture.getTexture());
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	@Override
	public HUDObject[] getHUDObjects() {
		return objects;
	}
}
