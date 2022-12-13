package engine.data.planetary;

import engine.data.entities.Tile;

public class Face {

	private int xPos,yPos;
	private int size;
	private Planet planet;

	private Tile[] tiles;

	public Face(int xPos, int yPos, Planet planet) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.planet = planet;
		size = planet.getSize();

		tiles = new Tile[size * size];
		for (int y=0; y<size; y++) {
			for (int x=0; x<size; x++) {
				int i = y*size + x;
				tiles[i] = new Tile(-1, x, y, this);
			}
		}
	}

	public Planet getPlanet() {
		return planet;
	}

	public int getX() {
		return xPos;
	}

	public int getY() {
		return yPos;
	}

	public int getSize() {
		return size;
	}

	public Tile[] getTiles() {
		return tiles;
	}

	public Tile getTile(int x, int y) {
		int i = y*size + x;
		return tiles[i];
	}
}
