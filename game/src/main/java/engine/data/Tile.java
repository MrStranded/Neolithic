package engine.data;

import engine.graphics.objects.planet.FacePart;

public class Tile {

	private int xPos,yPos;
	private int height = 0;

	private Face face;
	private FacePart tileMesh;

	public Tile(int xPos, int yPos, Face face) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.face = face;
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public Face getFace() {
		return face;
	}

	public FacePart getTileMesh() {
		return tileMesh;
	}
	public void setTileMesh(FacePart tileMesh) {
		this.tileMesh = tileMesh;
	}

	public int getX() {
		return xPos;
	}

	public int getY() {
		return yPos;
	}

	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
}
