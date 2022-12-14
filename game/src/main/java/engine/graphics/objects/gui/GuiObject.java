package engine.graphics.objects.gui;

import engine.graphics.gui.GuiData;
import engine.graphics.gui.RelativeParentPosition;
import engine.graphics.objects.GraphicalObject;
import engine.graphics.objects.models.Mesh;
import engine.math.numericalObjects.Vector2;
import engine.parser.utils.Logger;

import java.util.function.BiConsumer;

public class GuiObject extends GraphicalObject {

//	private int relativeScreenPositionX = RelativeParentPosition.LEFT;
//	private int relativeScreenPositionY = RelativeParentPosition.TOP;

	private Vector2 positionOnScreen = new Vector2(0, 0);
	private Vector2 sizeOnScreen = new Vector2(0, 0);

	private BiConsumer<GuiObject, RenderSpace> resizeCallback = null;

	private boolean influenceSizeCalculations = true;

	public GuiObject(Mesh mesh) {
		super(mesh);
		mesh.normalize();
	}
	public GuiObject() {}

	// ###################################################################################
	// ################################ Recalculations ###################################
	// ###################################################################################

	public void recalculate(RenderSpace renderSpace) {
		if (resizeCallback != null) {
			resizeCallback.accept(this, renderSpace);
		}

		recalculateScale();
	}

	private void recalculateScale() {
		double windowWidth = GuiData.getRenderWindow().getWidth();
		double windowHeight = GuiData.getRenderWindow().getHeight();
		double aspectRatio = windowWidth / windowHeight;

		// scale update

		double scaleX = 2d * aspectRatio * sizeOnScreen.getX() / windowWidth;
		double scaleY = 2d * sizeOnScreen.getY() / windowHeight;

		setScale(scaleX, scaleY, 1d);

		// position update

		double objectAbsX = positionOnScreen.getX();
//				+ RelativeParentPosition.getAbsOriginX(parentAbsWidth, absWidth, relativeScreenPositionX);

		double objectAbsY = positionOnScreen.getY()
//				+ RelativeParentPosition.getAbsOriginY(parentAbsHeight, absHeight, relativeScreenPositionY)
				+ sizeOnScreen.getY(); // since meshes are drawn from the lower left corner, and not like here from the top left

		double positionX = 2d * aspectRatio * objectAbsX / windowWidth - aspectRatio;
		double positionY = 2d * (windowHeight - objectAbsY) / windowHeight - 1d;

		setPosition(positionX, positionY, getPosition().getZ());
	}

	// ###################################################################################
	// ################################ Functionality ####################################
	// ###################################################################################

	public boolean isUnderMouse(double mouseX, double mouseY) {
		return mouseX >= positionOnScreen.getX()
				&& mouseX < positionOnScreen.getX() + sizeOnScreen.getX()
				&& mouseY >= positionOnScreen.getY()
				&& mouseY < positionOnScreen.getY() + sizeOnScreen.getY();
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public void setResizeCallback(BiConsumer<GuiObject, RenderSpace> resizeCallback) {
		this.resizeCallback = resizeCallback;
	}

	public void setInfluenceSizeCalculations(boolean influenceSizeCalculations) {
		this.influenceSizeCalculations = influenceSizeCalculations;
	}

	public boolean influencesSizeCalculations() {
		return influenceSizeCalculations;
	}

	public void setSizeOnScreen(double width, double height) {
		sizeOnScreen.setX(width);
		sizeOnScreen.setY(height);
	}

	public void setPositionOnScreen(double x, double y) {
		positionOnScreen.setX(x);
		positionOnScreen.setY(y);
	}

	public Vector2 getSizeOnScreen() {
		return sizeOnScreen;
	}

	// ###################################################################################
	// ################################ Debug ############################################
	// ###################################################################################

	public void debug(String prefix) {
		Logger.debug(prefix + "> " + toString());
		Logger.debug(prefix + "  position: " +  positionOnScreen);
		Logger.debug(prefix + "  size: " + sizeOnScreen);
	}

	public String toString() {
		return "GuiObject (influenceSizeCalc = " + influenceSizeCalculations + " " +
				"| position = " + positionOnScreen + " " +
				"| size = " + sizeOnScreen + ")";
	}

}
