package environment;

import java.awt.*;

/**
 * Created by michael1337 on 14/06/17.
 *
 *
 */
public class Tile {

	protected int x;
	protected int y;
	private int vx,vy;
	protected Face face;
	private int height=100;

	// ################################ Constructor
	public Tile() {

	}

	// ################################ Set Up
	public void setHeight(byte height) {
		this.height = height;
	}

	public void assignFace(Face face,int x,int y) {
		this.face = face;
		this.x = x;
		this.y = y;
	}

	/**Note: does not give the real position on the screen! Further calculation is required!*/
	public void calculateVisualPosition() {
		int size = face.getSize();
		vx = 2*x;
		vy = 2*y;

		if (x+y>=size) {
			int b,cx,cy;
			b=vy-vx;
			cx=(2*size-1-b)/2;
			cy=2*size-1-cx;

			vx=(2*cx-vx)+1;
			vy=(2*cy-vy)-2;
		}
	}

	public void lift (int lift) {
		synchronized(this) {
			if (lift>0) {
				height+=lift;
				if (height>255) height = 255;
			} else {
				height+=lift;
				if (height<0) height = 0;
			}
		}
	}

	// ################################ Getters
	/**getX returns the technical x location on the tile array of the face*/
	public int getX() { return x; }
	/**getY returns the technical y location on the tile array of the face*/
	public int getY() { return y; }

	/**getVX returns the visual x position on the screen
	 * Note: not the same as tx!*/
	public int getVX() { return vx; }
	/**getVY returns the visual y position on the screen
	 * Note: not the same as ty!*/
	public int getVY() { return vy; }

	public Face getFace() { return face; }

	public boolean getFlip() {
		return (x+y>=face.getSize());
	}

	public int getHeight() { return height; }

	public Color getColor() {
		return new Color (height,height,height);
	}

	public Color getColor(Color c) {
		return new Color (c.getRed()*height/255,c.getGreen()*height/255,c.getBlue()*height/255);
	}

}
