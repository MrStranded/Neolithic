package engine.math;

import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;
import engine.math.numericalObjects.Vector4;

public class Transformations {

	// ###################################################################################
	// ################################ Internal Axes ####################################
	// ###################################################################################

	public static Vector3[] getInternalAxes(Vector3 rotation) {

		Vector3[] out = new Vector3[3];

		Matrix4 rotationMatrix = Transformations.rotate(rotation);
		out[0] = rotationMatrix.times(new Vector4(1d,0,0,0)).extractVector3();
		out[1] = rotationMatrix.times(new Vector4(0,1d,0,0)).extractVector3();
		out[2] = rotationMatrix.times(new Vector4(0,0,1d,0)).extractVector3();

		return out;
	}

	public static Vector3 getInternalXAxis(Vector3 rotation) {

		Matrix4 rotationMatrix = Transformations.rotate(rotation);
		return rotationMatrix.times(new Vector4(1d,0,0,0)).extractVector3();
	}
	public static Vector3 getInternalYAxis(Vector3 rotation) {

		Matrix4 rotationMatrix = Transformations.rotate(rotation);
		return rotationMatrix.times(new Vector4(0,1d,0,0)).extractVector3();
	}
	public static Vector3 getInternalZAxis(Vector3 rotation) {

		Matrix4 rotationMatrix = Transformations.rotate(rotation);
		return rotationMatrix.times(new Vector4(0,0,1d,0)).extractVector3();
	}

	// ###################################################################################
	// ################################ Translate ########################################
	// ###################################################################################

	public static Matrix4 translate(Vector3 v) {

		return new Matrix4(
				1d, 0, 0, v.getX(),
				0, 1d, 0, v.getY(),
				0, 0, 1d, v.getZ(),
				0, 0, 0, 1d
		);
	}

	// ###################################################################################
	// ################################ Scale ############################################
	// ###################################################################################

	public static Matrix4 scale(Vector3 v) {

		return new Matrix4(
				v.getX(), 0, 0, 0,
				0, v.getY(), 0, 0,
				0, 0, v.getZ(), 0,
				0, 0, 0, 1d
		);
	}

	// ###################################################################################
	// ################################ Rotate ###########################################
	// ###################################################################################

	public static Matrix4 rotate(Vector3 v) {

		return rotateZ(v.getZ()).times(rotateY(v.getY()).times(rotateX(v.getX())));
	}

	public static Matrix4 rotateX(double a) {

		double c = Math.cos(a);
		double s = Math.sin(a);

		return new Matrix4(
				1d, 0, 0, 0,
				0, c, -s, 0,
				0, s, c, 0,
				0, 0, 0, 1d
		);
	}

	public static Matrix4 rotateY(double a) {

		double c = Math.cos(a);
		double s = Math.sin(a);

		return new Matrix4(
				c, 0, s, 0,
				0, 1d, 0, 0,
				-s, 0, c, 0,
				0, 0, 0, 1d
		);
	}

	public static Matrix4 rotateZ(double a) {

		double c = Math.cos(a);
		double s = Math.sin(a);

		return new Matrix4(
				c, -s, 0, 0,
				s, c, 0, 0,
				0, 0, 1d, 0,
				0, 0, 0, 1d
		);
	}

	/**
	 * Constructs a rotation matrix of the given angle around the given axis.
	 * Attention: axis has to be normalized!
	 * @param axis normalized rotation axis
	 * @param a angle in radian
	 * @return rotation matrix
	 */
	public static Matrix4 rotateAroundVector(Vector3 axis, double a) {

		double c = Math.cos(a);
		double s = Math.sin(a);
		// the 'R' in the variable name means 'reverse' or 'reciprocal'
		double cR = 1d - c;

		double x = axis.getX();
		double y = axis.getY();
		double z = axis.getZ();

		return new Matrix4(
				c + cR*x*x, x*y*cR - z*s, x*z*cR + y*s, 0,
				y*z*cR + z*s, c + cR*y*y, y*z*cR - x*s, 0,
				z*x*cR - y*s, z*y*cR + x*s, c + z*z*cR, 0,
				0,0,0,1d
		);
	}
}
