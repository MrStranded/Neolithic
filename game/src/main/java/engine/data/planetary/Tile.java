package engine.data.planetary;

import constants.TopologyConstants;
import engine.data.entities.Instance;
import engine.graphics.objects.planet.FacePart;
import engine.graphics.renderer.color.RGBA;

public class Tile extends Instance {

	private int xPos,yPos;
	private int height = 0;
	private int waterHeight = TopologyConstants.PLANET_OZEAN_HEIGHT;

	private RGBA color = TopologyConstants.TILE_DEFAULT_COLOR;

	private Face face;
	private FacePart tileMesh;

	public Tile(int id, int xPos, int yPos, Face face) {
		super(id);
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

	public int getWaterHeight() {
		return waterHeight;
	}
	public void setWaterHeight(int waterHeight) {
		this.waterHeight = waterHeight;
	}

	public RGBA getColor() {
		return color;
	}
	public void setColor(RGBA color) {
		this.color = color;
	}
}
