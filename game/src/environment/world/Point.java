package environment.world;

/**
 * Created by michael1337 on 14/06/17.
 *
 *
 */
public class Point {

	private double x,y,z;
	private byte worldSheet; // 0->0 bis 3, 1->4 bis 7, 2->8 bis 11

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

	// ################################ Getters
	public double getX() { return x; }
	public double getY() { return y; }
	public double getZ() { return z; }

	/**optional parameter.
	 * The world sheet corresponds to the three planes, on which the points of a ikosaeder lie.
	 * 0->0 bis 3
	 * 1->4 bis 7
	 * 2->8 bis 11
	 */
	public byte getWorldSheet() { return worldSheet; }

	// ################################ Setters
	public void setWorldSheet(byte worldSheet) { this.worldSheet = worldSheet; }

}
