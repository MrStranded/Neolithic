package main.environment.world;

/**
 * Created by michael1337 on 14/06/17.
 *
 *
 */
public class Point {

	private double x,y,z;
	private byte worldSheet; // 0->0 till 3, 1->4 till 7, 2->8 till 11

	public Point (double x,double y,double z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}

	public Point (double x,double y,double z,byte worldSheet) {
		this.x=x;
		this.y=y;
		this.z=z;
		this.worldSheet=worldSheet;
	}

	public Point copy() {
		return new Point(x,y,z);
	}

	// ################################ Math

	public Point add (Point p) {
		return new Point(x+p.getX(), y+p.getY(), z+p.getZ());
	}

	public Point subtract (Point p) {
		return new Point(x-p.getX(), y-p.getY(), z-p.getZ());
	}

	public Point multiply (double f) {
		return new Point(x*f, y*f, z*f);
	}

	public Point divide (double f) {
		return new Point(x/f, y/f, z/f);
	}

	// ################################ Getters
	public double getX() { return x; }
	public double getY() { return y; }
	public double getZ() { return z; }

	public float getXf() { return (float) x; }
	public float getYf() { return (float) y; }
	public float getZf() { return (float) z; }

	/**optional parameter.
	 * The world sheet corresponds to the three planes, on which the points of a ikosaeder lie.
	 * 0->0 till 3
	 * 1->4 till 7
	 * 2->8 till 11
	 */
	public byte getWorldSheet() { return worldSheet; }

	// ################################ Setters
	public void setWorldSheet(byte worldSheet) { this.worldSheet = worldSheet; }

}
