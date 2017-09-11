package environment.world;

import environment.meteorology.RainDrop;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by michael1337 on 14/06/17.
 *
 *
 */
public class Tile {

	private int x;
	private int y;
	private int vx,vy; // visual position. For example Tile(x,y) has the same visual position as Tile(size-x,size-y)
	private Face face;
	private int height=0; // default height of a Tile

	private int red=255,green=255,blue=255;

	private int humidity = 0;
	private ArrayList<RainDrop> rain = new ArrayList<RainDrop>();
	private ConcurrentLinkedDeque<Entity> entities = new ConcurrentLinkedDeque<Entity>(); // contained entities

	private Entity self; // the entity that is responsible for the tile itself

	/**
	 * Creates a Tile.
	 */
	public Tile() {
		red = 192+(int) (Math.random()*64);
		green = 192+(int) (Math.random()*48);
		blue = 100+(int) (Math.random()*64);
	}

	// ###################################################################################
	// ################################ Set Up ###########################################
	// ###################################################################################

	/**
	 * Assigns a Face to this Tile and also sets the coordinates of the Tile.
	 * @param face parent Face
	 * @param x coordinate of Tile
	 * @param y coordinate of Tile
	 */
	public void assignFace(Face face,int x,int y) {
		this.face = face;
		this.x = x;
		this.y = y;
	}

	/**
	 * Note: does not give the real position on the screen! Further calculation is required!
	 */
	public void calculateVisualPosition() {
		int size = face.getSize();
		if (x+y >= size) {
			vx = size-1-x;
			vy = size-1-y;
		} else {
			vx = x;
			vy = y;
		}
	}

	public void createRainDrop() {
		rain.add(new RainDrop());
	}

	// ###################################################################################
	// ################################ Modification #####################################
	// ###################################################################################

	public void addEntity(Entity entity) {
		entities.add(entity);
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################


	public ConcurrentLinkedDeque<Entity> getEntities() {
		return entities;
	}
	public void setEntities(ConcurrentLinkedDeque<Entity> entities) {
		this.entities = entities;
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

	/**getX returns the technical x location on the tile array of the face*/
	public int getX() { return x; }
	/**getY returns the technical y location on the tile array of the face*/
	public int getY() { return y; }

	/** getVX returns the visual x position on the screen*/
	public int getVX() { return vx; }
	/** getVY returns the visual y position on the screen*/
	public int getVY() { return vy; }

	public Face getFace() { return face; }

	public boolean isFlipped() {
		return (x+y>=face.getSize());
	}

	public int getHeight() { return height; }
	public void setHeight(int height) {
		if (height > 255) height = 255;
		if (height < 0) height = 0;
		this.height = height;
	}

	public int getHumidity() {
		return humidity;
	}
	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}

	public ArrayList<RainDrop> getRain() {
		return rain;
	}
}
