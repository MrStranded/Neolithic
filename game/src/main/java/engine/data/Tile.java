package engine.data;

public class Tile {

	private int xPos,yPos;
	private int height = 0;

	private Face face;

	public Tile(int xPos, int yPos, Face face) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.face = face;
	}

	public Face getFace() {
		return face;
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
