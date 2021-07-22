package engine.graphics.objects.gui;

import engine.graphics.gui.GuiData;
import engine.graphics.gui.RelativeParentPosition;
import engine.graphics.objects.GraphicalObject;
import engine.graphics.objects.models.Mesh;

public class GUIObject extends GraphicalObject {

	private int relativeScreenPositionX = RelativeParentPosition.LEFT;
	private int relativeScreenPositionY = RelativeParentPosition.TOP;

	private double xRelPos = 0, yRelPos = 0;
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

		double objectAbsX = xRelPos * parentAbsWidth + RelativeParentPosition.getAbsOriginX(parentAbsWidth, absWidth, relativeScreenPositionX);
		double objectAbsY = yRelPos * parentAbsHeight + RelativeParentPosition.getAbsOriginY(parentAbsHeight, absHeight, relativeScreenPositionY);

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

	public void setRelativeLocation(double xPos, double yPos) {
		this.xRelPos = xPos;
		this.yRelPos = yPos;
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

	public double getxRelPos() {
		return xRelPos;
	}
	public void setxRelPos(double xRelPos) {
		this.xRelPos = xRelPos;
	}

	public double getyRelPos() {
		return yRelPos;
	}
	public void setyRelPos(double yRelPos) {
		this.yRelPos = yRelPos;
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
