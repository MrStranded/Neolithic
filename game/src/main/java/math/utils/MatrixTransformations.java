package math.utils;

import math.Matrix4;
import math.Vector3;

public class MatrixTransformations {

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
}
