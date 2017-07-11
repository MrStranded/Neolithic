package environment;

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

	/**
	 * Creates a Tile.
	 */
	public Tile() {

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

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################

	/**getX returns the technical x location on the tile array of the face*/
	public int getX() { return x; }
	/**getY returns the technical y location on the tile array of the face*/
	public int getY() { return y; }

	/**
	 * getVX returns the visual x position on the screen
	 */
	public int getVX() { return vx; }
	/**
	 * getVY returns the visual y position on the screen
	 */
	public int getVY() { return vy; }

	public Face getFace() { return face; }

	public boolean isFlipped() {
		return (x+y>=face.getSize());
	}

	public int getHeight() { return height; }
	public void setHeight(int height) {
		this.height = height;
	}

}
