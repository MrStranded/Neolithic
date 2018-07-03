package main.engine.graphics.math;

/**
 * Created by michael1337 on 02/07/18.
 *
 * Homogeneous coordinates.
 */
public class Vector4 {

	private final double x,y,z,w;

	public Vector4(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vector4(Vector4 other) {
		x = other.x;
		y = other.y;
		z = other.z;
		w = other.w;
	}

	// ###################################################################################
	// ################################ Math #############################################
	// ###################################################################################

	public Vector4 plus (Vector4 other) {
		return new Vector4(x+other.x, y+other.y, z+other.z, w+other.w);
	}

	public Vector4 minus (Vector4 other) {
		return new Vector4(x-other.x, y-other.y, z-other.z, w-other.w);
	}

	public Vector4 times (double t) { return new Vector4(x*t,y*t,z*t,w*t); }

	/**
	 * Ensures that the vector's w field is 1 and divides the other fields by w.
	 * If w is zero, it is set to 1.
	 * @return corresponding vector with w == 1
	 */
	public Vector4 standardize() {
		if (w == 0) {
			return new Vector4(x,y,z,1);
		} else {
			return new Vector4(x/w,y/w,z/w,1);
		}
	}

	// ###################################################################################
	// ################################ Test #############################################
	// ###################################################################################

	public boolean equals (Vector4 other) {
		return ((x==other.x)&&(y==other.y)&&(z==other.z)&&(w==other.w));
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public double getX() { return x; }
	public double getY() { return y; }
	public double getZ() { return z; }
	public double getW() { return w; }

}
