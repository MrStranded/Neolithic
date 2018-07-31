package engine.graphics.objects.gui;

import engine.graphics.gui.RelativeScreenPosition;
import engine.graphics.objects.GraphicalObject;
import engine.graphics.objects.models.Mesh;

public class GUIObject extends GraphicalObject {

	private int relativeScreenPositionX = RelativeScreenPosition.LEFT;
	private int relativeScreenPositionY = RelativeScreenPosition.TOP;

	private double xPos = 0, yPos = 0;
	private double width = 0, height = 0;

	public GUIObject(Mesh mesh) {
		super(mesh);
		mesh.normalize();
	}
	public GUIObject() {}

	public void recalculateScale(double windowWidth, double windowHeight) {
		double aspectRatio = windowWidth / windowHeight;

		double scaleX = 2d * aspectRatio * width / windowWidth;
		double scaleY = 2d * height / windowHeight;

		setScale(scaleX, scaleY, 1d);

		double objectX = xPos + RelativeScreenPosition.getOriginX(windowWidth, width, relativeScreenPositionX);
		double objectY = yPos + RelativeScreenPosition.getOriginY(windowHeight, height, relativeScreenPositionY);

		objectY += height; // since meshes are drawn from the lower left corner, and not like here from the top left

		double positionX = 2d * aspectRatio * objectX / windowWidth - aspectRatio;
		double positionY = 2d * (windowHeight - objectY) / windowHeight - 1d;

		setPosition(positionX, positionY, getPosition().getZ());
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
	}

	public void setLocation(double xPos, double yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
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

	public double getxPos() {
		return xPos;
	}
	public void setxPos(double xPos) {
		this.xPos = xPos;
	}

	public double getyPos() {
		return yPos;
	}
	public void setyPos(double yPos) {
		this.yPos = yPos;
	}

	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
}
