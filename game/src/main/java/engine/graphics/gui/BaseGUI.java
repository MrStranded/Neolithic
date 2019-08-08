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
		int slot = 3;

		List<Integer> attributes = Data.getAllAttributeIDs();
		double nrOfAtts = attributes.size();
		List<Instance> monkeys = Data.getAllInstancesWithID(Data.getContainerID("cMonkey"));
		double headcount = monkeys.size();
		if (nrOfAtts == 0 || headcount == 0) { return; }

		int sleepingID = Data.getProtoAttributeID("attSleeping");

		for (Integer id : attributes) {
			double sum = 0;
			for (Instance monkey : monkeys) {
				sum += monkey.getAttributeValue(id);
			}

			if (id == sleepingID) {
				GUIObject sleepers = new TextObject("Sleeping: " + ((int) sum), fontTexture);
				sleepers.setSize(windowWidth/4, 30);
				sleepers.setLocation(windowWidth/2, 0);
				sleepers.recalculateScale(windowWidth, windowHeight);
				objects[0] = sleepers;
				GUIObject sleepers2 = new TextObject(" of: " + ((int) headcount), fontTexture);
				sleepers2.setSize(windowWidth/4, 30);
				sleepers2.setLocation(windowWidth*3/4, 0);
				sleepers2.recalculateScale(windowWidth, windowHeight);
				objects[1] = sleepers2;
			}

			double yPos = ((double) (slot) / (nrOfAtts+1d)) * windowHeight;
			GUIObject out = new TextObject(Data.getProtoAttribute(id).getName() + ":   " + ((double) ((int) (sum/headcount*100d)) / 100d), fontTexture);

			out.setSize(windowWidth/3d,windowHeight/(nrOfAtts+1d));
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

		double nrOfChildren = 0;
		for (Instance monkey : monkeys) {
			nrOfChildren += monkey.getAttributeValue(Data.getContainerID("attAge")) >= monkey.getAttributeValue(Data.getContainerID("attMatureAge")) ? 0 : 1;
		}

		GUIObject sleepers = new TextObject("Children: " + ((int) nrOfChildren), fontTexture);
		sleepers.setSize(windowWidth/4, 30);
		sleepers.setLocation(windowWidth/2, 30);
		sleepers.recalculateScale(windowWidth, windowHeight);
		objects[2] = sleepers;
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
