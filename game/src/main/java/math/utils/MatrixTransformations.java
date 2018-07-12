package math.utils;

import math.Matrix4;
import math.Vector3;

public class MatrixTransformations {

	public static Matrix4 translate(Vector3 v) {

		return new Matrix4(
				1d, 0, 0, v.getX(),
				0, 1d, 0, v.getY(),
				0, 0, 1d, v.getZ(),
				0, 0, 0, 1d
		);
	}

	public static Matrix4 scale(Vector3 v) {

		return new Matrix4(
				v.getX(), 0, 0, 0,
				0, v.getY(), 0, 0,
				0, 0, v.getZ(), 0,
				0, 0, 0, 1d
		);
	}

	public static Matrix4 rotate(Vector3 v) {

		return new Matrix4();
	}
}
