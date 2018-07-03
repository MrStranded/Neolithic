package math;

/**
 * Created by michael1337 on 02/07/18.
 */
public class Vector3 {

	private double x,y,z;

	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3(Vector3 other) {
		x = other.x;
		y = other.y;
		z = other.z;
	}

	// ###################################################################################
	// ################################ Math #############################################
	// ###################################################################################

	public Vector3 plus(Vector3 other) {
		return new Vector3(x+other.x, y+other.y, z+other.z);
	}

	public Vector3 plusInplace(Vector3 other) {
		x += other.x;
		y += other.y;
		z += other.z;
		return this;
	}

	public Vector3 minus(Vector3 other) {
		return new Vector3(x-other.x, y-other.y, z-other.z);
	}

	public Vector3 minusInplace(Vector3 other) {
		x -= other.x;
		y -= other.y;
		z -= other.z;
		return this;
	}

	public Vector3 times(double t) { return new Vector3(x*t,y*t,z*t); }

	public Vector3 timesInplace(double t) {
		x *= t;
		y *= t;
		z *= t;
		return this;
	}

	public double dot(Vector3 other) {
		return x*other.x + y*other.y + z*other.z;
	}

	public Vector3 cross(Vector3 other) {
		return new Vector3(
				y*other.z - other.y*z,
				z*other.x - other.z*x,
				x*other.y - other.x*y);
	}

	public Vector3 crossInplace(Vector3 other) {
		double newX = y*other.z - other.y*z;
		double newY = z*other.x - other.z*x;
		double newZ = x*other.y - other.x*y;
		x = newX; y = newY; z = newZ;
		return this;
	}

	public double length() {
		return Math.sqrt(x*x + y*y + z*z);
	}
	public double lengthSquared() {
		return x*x + y*y + z*z;
	}

	// ###################################################################################
	// ################################ Test #############################################
	// ###################################################################################

	public boolean equals(Vector3 other) {
		return ((x==other.x)&&(y==other.y)&&(z==other.z));
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public double getX() { return x; }
	public double getY() { return y; }
	public double getZ() { return z; }

}
