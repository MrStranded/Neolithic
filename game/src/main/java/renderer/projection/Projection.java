package renderer.projection;

import math.Matrix4;

public class Projection {

	public static Matrix4 createProjectionMatrix(double fieldOfView, double aspectRatio, double zNear, double zFar) {

		double zp = zFar + zNear;
		double zm = zFar - zNear;

		double v2 = 1d/Math.tan(fieldOfView/2d);
		double v1 = v2/aspectRatio;
		double v3 = -zp/zm;
		double v4 = -(2d*zFar*zNear)/zm;

	    return new Matrix4(
	        v1, 0, 0, 0,
	        0, v2, 0, 0,
	        0, 0, v3, v4,
	        0, 0, -1d, 0
	    );
		//return new Matrix4();
	}
}
