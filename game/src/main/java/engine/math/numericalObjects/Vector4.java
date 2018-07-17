package engine.math.numericalObjects;

/**
 * Created by michael1337 on 02/07/18.
 *
 * Homogeneous coordinates.
 */
public class Vector4 {

	private double x,y,z,w;

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

	public Vector4(Vector3 other) {
		x = other.getX();
		y = other.getY();
		z = other.getZ();
		w = 1;
	}

	// ###################################################################################
	// ################################ Math #############################################
	// ###################################################################################

	public Vector4 plus(Vector4 other) {
		return new Vector4(x+other.x, y+other.y, z+other.z, w+other.w);
	}

	public Vector4 plusInplace(Vector4 other) {
		x += other.x;
		y += other.y;
		z += other.z;
		w += other.w;
		return this;
	}

	public Vector4 minus(Vector4 other) {
		return new Vector4(x-other.x, y-other.y, z-other.z, w-other.w);
	}

	public Vector4 minusInplace(Vector4 other) {
		x -= other.x;
		y -= other.y;
		z -= other.z;
		w -= other.w;
		return this;
	}

	public Vector4 times(double t) { return new Vector4(x*t,y*t,z*t,w*t); }

	public Vector4 timesInplace(double t) {
		x *= t;
		y *= t;
		z *= t;
		w *= t;
		return this;
	}

	/**
	 * Ensures that the vector's w field is 1 and divides the other fields by w.
	 * This method throws an ArithmeticException when w == 0.
	 * @return corresponding vector with w == 1
	 */
	public Vector4 standardize() throws ArithmeticException {
		if (w == 0) {
			throw new ArithmeticException("W Value of vector is zero. Cannot divide by zero.");
		} else {
			return new Vector4(x/w,y/w,z/w,1);
		}
	}

	/**
	 * Ensures that the vector's w field is 1 and divides the other fields by w.
	 * This method throws an ArithmeticException when w == 0.
	 * Note that this method operates on the current vector and changes its values.
	 * @return corresponding vector with w == 1
	 */
	public Vector4 standardizeInplace() throws ArithmeticException {
		if (w == 0) {
			throw new ArithmeticException("W Value of vector is zero. Cannot divide by zero.");
		} else {
			x /= w;
			y /= w;
			z /= w;
			w = 1;
			return this;
		}
	}

	/**
	 * Cuts out x,y and z and puts them into a Vector3.
	 * Does not consider value of w!
	 * @return just mentioned Vector3
	 */
	public Vector3 extractVector3() {
		return new Vector3(x,y,z);
	}

	// ###################################################################################
	// ################################ Test #############################################
	// ###################################################################################

	public boolean equals (Vector4 other) {
		return ((x==other.x)&&(y==other.y)&&(z==other.z)&&(w==other.w));
	}

	// ###################################################################################
	// ################################ Printing #########################################
	// ###################################################################################

	public String toString() {
		return "(x: "+x+" ,y: "+y+" ,z: "+z+" ,w: "+w+")";
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public double getX() { return x; }
	public double getY() { return y; }
	public double getZ() { return z; }
	public double getW() { return w; }

}
