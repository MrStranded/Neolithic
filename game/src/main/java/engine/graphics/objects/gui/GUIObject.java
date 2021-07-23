package engine.graphics.objects.gui;

import engine.graphics.gui.GuiData;
import engine.graphics.gui.RelativeParentPosition;
import engine.graphics.objects.GraphicalObject;
import engine.graphics.objects.models.Mesh;

public class GUIObject extends GraphicalObject {

	private int relativeScreenPositionX = RelativeParentPosition.LEFT;
	private int relativeScreenPositionY = RelativeParentPosition.TOP;

	private double xRelOffset = 0, yRelOffset = 0;
	private double xAbsOffset = 0, yAbsOffset = 0;
	private double absWidth = 0, absHeight = 0;

	public GUIObject(Mesh mesh) {
		super(mesh);
		mesh.normalize();
	}
	public GUIObject() {}

	public void recalculateScale(double parentAbsWidth, double parentAbsHeight) {
		double aspectRatio = (double) GuiData.getRenderWindow().getWidth() / (double) GuiData.getRenderWindow().getHeight();

		double scaleX = 2d * aspectRatio * absWidth / parentAbsWidth;
		double scaleY = 2d * absHeight / parentAbsHeight;

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
	// ################################ Getters and Setters ##############################
	// ###################################################################################

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
	public void setxRelOffset(double xRelOffset) {
		this.xRelOffset = xRelOffset;
	}

	public double getyRelOffset() {
		return yRelOffset;
	}
	public void setyRelOffset(double yRelOffset) {
		this.yRelOffset = yRelOffset;
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
}
