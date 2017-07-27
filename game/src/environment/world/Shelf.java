package environment.world;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by Michael on 14.07.2017.
 *
 * A slice of a tile.
 */
public class Shelf {

	private Tile tile;
	private int red,green,blue;

	public Shelf(Tile tile) {
		this.tile = tile;
		red = 200 + (int) (Math.random()*42d);
		green = 150 + (int) (Math.random()*42d);
		blue = 50 + (int) (Math.random()*42d);
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################

	public Tile getTile() {
		return tile;
	}
	public void setTile(Tile tile) {
		this.tile = tile;
	}

	public int getRed() {
		return red;
	}
	public void setRed(int red) {
		this.red = red;
	}

	public int getGreen() {
		return green;
	}
	public void setGreen(int green) {
		this.green = green;
	}

	public int getBlue() {
		return blue;
	}
	public void setBlue(int blue) {
		this.blue = blue;
	}
}
