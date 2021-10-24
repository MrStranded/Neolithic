package engine.graphics.objects.gui;

import engine.data.entities.GuiElement;
import engine.graphics.gui.GuiData;
import engine.graphics.gui.RelativeParentPosition;
import engine.graphics.objects.GraphicalObject;
import engine.graphics.objects.models.Mesh;
import engine.parser.utils.Logger;

import java.util.function.BiConsumer;

public class GuiObject extends GraphicalObject {

	private int relativeScreenPositionX = RelativeParentPosition.LEFT;
	private int relativeScreenPositionY = RelativeParentPosition.TOP;

	private double xRelOffset = 0, yRelOffset = 0;
	private double xAbsOffset = 0, yAbsOffset = 0;
	private double absWidth = 0, absHeight = 0;

	private BiConsumer<GuiObject, GuiElement> resizeCallback = null;

	private boolean influenceSizeCalculations = true;

	public GuiObject(Mesh mesh) {
		super(mesh);
		mesh.normalize();
	}
	public GuiObject() {}

	// ###################################################################################
	// ################################ Recalculations ###################################
	// ###################################################################################

	public void resize(GuiElement element) {
		if (resizeCallback == null) { return; }

		resizeCallback.accept(this, element);
	}

	public void recalculateScale(double parentAbsWidth, double parentAbsHeight) {
		double windowWidth = GuiData.getRenderWindow().getWidth();
		double windowHeight = GuiData.getRenderWindow().getHeight();
		double aspectRatio = windowWidth / windowHeight;

		double scaleX = 2d * aspectRatio * absWidth / windowWidth;
		double scaleY = 2d * absHeight / windowHeight;

		setScale(scaleX, scaleY, 1d);

		double objectAbsX = xAbsOffset
				+ xRelOffset * parentAbsWidth
				+ RelativeParentPosition.getAbsOriginX(parentAbsWidth, absWidth, relativeScreenPositionX);
		double objectAbsY = yAbsOffset
				+ yRelOffset * parentAbsHeight
				+ RelativeParentPosition.getAbsOriginY(parentAbsHeight, absHeight, relativeScreenPositionY);

		objectAbsY += absHeight; // since meshes are drawn from the lower left corner, and not like here from the top left

		double positionX = 2d * aspectRatio * objectAbsX / parentAbsWidth - aspectRatio;
		double positionY = 2d * (parentAbsHeight - objectAbsY) / parentAbsHeight - 1d;

		setPosition(positionX, positionY, getPosition().getZ());
	}

	// ###################################################################################
	// ################################ Functionality ####################################
	// ###################################################################################

	public boolean isUnderMouse(double mouseX, double mouseY, double parentAbsWidth, double parentAbsHeight) {
		double x = (xAbsOffset + xRelOffset * parentAbsWidth);
		double y = (yAbsOffset + yRelOffset * parentAbsHeight);

		return mouseX >= x
				&& mouseX < x + absWidth
				&& mouseY >= y
				&& mouseY < y + absHeight;
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public void setResizeCallback(BiConsumer<GuiObject, GuiElement> resizeCallback) {
		this.resizeCallback = resizeCallback;
	}

	public void setInfluenceSizeCalculations(boolean influenceSizeCalculations) {
		this.influenceSizeCalculations = influenceSizeCalculations;
	}

	public boolean isInfluenceSizeCalculations() {
		return influenceSizeCalculations;
	}

	public void setAbsoluteSize(double width, double height) {
		this.absWidth = width;
		this.absHeight = height;
	}

	public void setRelativeOffset(double xPos, double yPos) {
		this.xRelOffset = xPos;
		this.yRelOffset = yPos;
	}
	public void setAbsoluteOffset(double xPos, double yPos) {
		this.xAbsOffset = xPos;
		this.yAbsOffset = yPos;
	}

	public int getRelativeScreenPositionX() {
		return relativeScreenPositionX;
	}
	public void setRelativeScreenPositionX(int relativeScreenPositionX) {
		this.relativeScreenPositionX = relativeScreenPositionX;
	}

	public int getRelativeScreenPositionY() {
		return relativeScreenPositionY;
	}
	public void setRelativeScreenPositionY(int relativeScreenPositionY) {
		this.relativeScreenPositionY = relativeScreenPositionY;
	}

	public void setRelativeScreenPosition(int horizontal, int vertical) {
		relativeScreenPositionX = horizontal;
		relativeScreenPositionY = vertical;
	}

	public double getxRelOffset() {
		return xRelOffset;
	}

	public double getyRelOffset() {
		return yRelOffset;
	}

	public double getAbsWidth() {
		return absWidth;
	}
	public void setAbsWidth(double absWidth) {
		this.absWidth = absWidth;
	}

	public double getAbsHeight() {
		return absHeight;
	}
	public void setAbsHeight(double absHeight) {
		this.absHeight = absHeight;
	}

	// ###################################################################################
	// ################################ Debug ############################################
	// ###################################################################################

	public void debug(String prefix) {
		Logger.debug(prefix + "> " + toString());
		Logger.debug(prefix + "  position: " +  xAbsOffset + " + " + xRelOffset + ", " + yAbsOffset + " + " + yRelOffset);
		Logger.debug(prefix + "  width: " + getAbsWidth());
		Logger.debug(prefix + "  height: " + getAbsHeight());
	}
}
