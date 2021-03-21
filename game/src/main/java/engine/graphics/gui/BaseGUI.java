package engine.graphics.gui;

import engine.data.Data;
import engine.data.attributes.Attribute;
import engine.data.entities.Instance;
import engine.data.options.GameOptions;
import engine.data.proto.Container;
import engine.data.proto.ProtoAttribute;
import engine.data.structures.trees.binary.BinaryTree;
import engine.graphics.objects.generators.MeshGenerator;
import engine.graphics.objects.gui.GUIObject;
import engine.graphics.objects.gui.TextObject;
import engine.graphics.objects.textures.FontTexture;
import engine.parser.utils.Logger;
import load.TextureLoader;

import java.awt.*;
import java.util.List;

public class BaseGUI implements GUIInterface {

	private GUIObject[] objects;
	private FontTexture fontTexture;

	private final int MAX_OBJECTS = 64;
	private int objectCounter = 0;
	private int yPos = 0;

	public BaseGUI() {
		objects = new GUIObject[MAX_OBJECTS];
		initializeTestObjects();
	}

	private void initializeTestObjects() {
//		GUIObject shadowMap = new GUIObject(MeshGenerator.createQuad());
//		shadowMap.getMesh().getMaterial().setTexture(TextureLoader.loadTexture("data/mods/vanilla/assets/textures/trollface.png"));
//		shadowMap.setSize(200,200);
//		shadowMap.setLocation(0,100);
//		addHUDObject(shadowMap);

		try {
			fontTexture = new FontTexture(new Font("Arial", Font.PLAIN, 50), "US-ASCII"); // UTF-8 , UTF-16 , US-ASCII , ISO-8859-1 (the utf charsets don't work)
		} catch (Exception e) {
			e.printStackTrace();
		}
		GUIObject banner = new TextObject("Neolithic - A Stone Age Game", fontTexture);
		banner.setSize(600,30);
//		banner.setPosition(0,0,0.5d);
//		banner.setPosition(0,0,-0.25d);
		banner.setLocation(0,0);
		banner.setRelativeScreenPositionX(RelativeScreenPosition.CENTER);
		addHUDObject(banner);
	}

	// ###################################################################################
	// ################################ Functionality ####################################
	// ###################################################################################

	public void tick(int windowWidth, int windowHeight) {
//		clear();

//		Container currentSelection = Data.getContainer(GameOptions.currentContainerId).orElse(null);
//		GUIObject selection = new TextObject(currentSelection != null ? currentSelection.getName() : String.valueOf(GameOptions.currentContainerId), fontTexture);
//		selection.setSize(windowWidth * 0.25, windowHeight * 0.125);
//		selection.setLocation(windowWidth * 0.75, windowHeight * 0.875);
//		selection.setRelativeScreenPosition(RelativeScreenPosition.LEFT, RelativeScreenPosition.TOP);
////		selection.setLocation(windowWidth * 3.0/4.0, windowHeight * 7.0/8.0);
//		selection.recalculateScale(windowWidth, windowHeight);
//		addHUDObject(selection);

//		if (GameOptions.selectedInstance != null && ! GameOptions.selectedInstance.isSlatedForRemoval()) {
//			yPos = 0;
//			printInstance(GameOptions.selectedInstance, windowWidth, windowHeight);
//		}
	}

	private void printInstance(Instance instance, int windowWidth, int windowHeight) {
		if (instance != null) {
			yPos = printInfo(instance.getName() + " on " + instance.getPosition(), windowWidth, windowHeight, yPos);

			BinaryTree<Attribute> tree = instance.getAttributes();
			if (tree != null) {
				tree.forEach(attribute -> {
					ProtoAttribute protoAttribute = Data.getProtoAttribute(attribute.getId());
					yPos = printInfo(protoAttribute.getName() + ": " + instance.getAttributeValue(attribute.getId()), windowWidth, windowHeight, yPos);
				});
			}

			List<Instance> subs = instance.getSubInstances();
			if (subs != null) {
				try {
					for (Instance sub : subs) {
						yPos += 10;
						printInstance(sub, windowWidth, windowHeight);
					}
				} catch (Exception e) {
					// a concurrent modification exception
					Logger.error("Concurrent modification exception during BaseGUI drawing");
				}
			}
		}
	}

	private int printInfo(String info, int windowWidth, int windowHeight, int yPos) {
		GUIObject text = new TextObject(info, fontTexture);
		text.setSize(windowWidth/4, windowHeight/32);
		text.setLocation(0, yPos);
		text.recalculateScale(windowWidth, windowHeight);
		addHUDObject(text);
		return yPos + windowHeight/32;
	}

	// ###################################################################################
	// ################################ Accessing ########################################
	// ###################################################################################

	private void clear() {
		objectCounter = 0;
		for (int i=0; i<objects.length; i++) {
			if (objects[i] != null) { objects[i].cleanUp(); }
			objects[i] = null;
		}
	}

	public void addHUDObject(GUIObject guiObject) {
		if (objects[objectCounter] != null) {
			objects[objectCounter].cleanUp();
		}
		objects[objectCounter] = guiObject;

		objectCounter = (objectCounter + 1) % MAX_OBJECTS;
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
