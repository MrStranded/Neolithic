package engine.graphics.gui;

import engine.data.Data;
import engine.data.entities.Instance;
import engine.graphics.objects.gui.GUIObject;
import engine.graphics.objects.gui.TextObject;
import engine.graphics.objects.textures.FontTexture;

import java.awt.*;
import java.util.List;

public class BaseGUI implements GUIInterface {

	private GUIObject[] objects;
	private FontTexture fontTexture;

	private final int MAX_OBJECTS = 32;
	private int objectCounter = 0;

	public BaseGUI() {
		objects = new GUIObject[MAX_OBJECTS];
		initializeTestObjects();
	}

	private void initializeTestObjects() {
		try {
			fontTexture = new FontTexture(new Font("Arial", Font.PLAIN, 50), "ISO-8859-1"); // UTF-8 , UTF-16 , US-ASCII , ISO-8859-1 (the utf charsets don't work)
		} catch (Exception e) {
			e.printStackTrace();
		}
		GUIObject banner = new TextObject("Neolithic - A Stone Age Game", fontTexture);
		banner.setSize(600,30);
		banner.setPosition(0,0,0.5d);
		banner.setPosition(0,0,-0.25d);
		banner.setRelativeScreenPositionX(RelativeScreenPosition.CENTER);
		addHUDObject(banner);

		/*objects[0] = new GUIObject(MeshGenerator.createQuad());
		objects[0].getMesh().getMaterial().setTexture(TextureLoader.loadTexture("data/mods/vanilla/assets/textures/trollface.png"));
		objects[0].setSize(200,200);
		//objects[0].setLocation(0,100);
		objects[0].placeInto(0,0,-0.5d);
		//objects[0].getMesh().setTopColor(0.5f, 0.5f, 0.5f, 0.5f);
		//objects[0].setLocation(200, 400);
		//objects[0].setRelativeScreenPosition(RelativeScreenPosition.RIGHT, RelativeScreenPosition.BOTTOM);
		objects[0].setRelativeScreenPosition(RelativeScreenPosition.LEFT, RelativeScreenPosition.TOP);*/
	}

	// ###################################################################################
	// ################################ Functionality ####################################
	// ###################################################################################

	public void tick(int windowWidth, int windowHeight) {
		int slot = 1;
		double width = windowWidth;
		double height = windowHeight;

		List<Integer> attributes = Data.getAllAttributeIDs();
		double nrOfAtts = attributes.size();
		List<Instance> monkeys = Data.getAllInstancesWithID(Data.getContainerID("cMonkey"));
		double headcount = monkeys.size();
		if (nrOfAtts == 0 || headcount == 0) { return; }

		for (Integer id : attributes) {
			double sum = 0;
			for (Instance monkey : monkeys) {
				sum += monkey.getAttributeValue(id);
			}

			double yPos = ((double) (slot) / (nrOfAtts+1d)) * height;
			GUIObject out = new TextObject(Data.getProtoAttribute(id).getName() + ":   " + ((double) ((int) (sum/headcount*100d)) / 100d), fontTexture);

			out.setSize(width/2d,height/(nrOfAtts+1d));
			out.setLocation(0, yPos);
			out.recalculateScale(windowWidth,windowHeight);

			if (objects[slot] != null) {
				objects[slot].cleanUp();
			}
			objects[slot] = out;

			if (++slot >= MAX_OBJECTS) {
				slot = 1;
			}
		}
	}

	// ###################################################################################
	// ################################ Accessing ########################################
	// ###################################################################################

	public void addHUDObject(GUIObject guiObject) {
		if (objects[objectCounter] != null) {
			objects[objectCounter].cleanUp();
		}
		objects[objectCounter] = guiObject;

		if (++objectCounter >= MAX_OBJECTS) {
			objectCounter = 0;
		}
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
