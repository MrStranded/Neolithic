package engine.renderer.projection;

import math.Matrix4;

public class Projection {

	public static Matrix4 createPerspectiveProjectionMatrix(double left, double right, double top, double bottom, double near, double far) {

		Matrix4 m = new Matrix4();

		m.setA11(2d*near / (right-left));
		m.setA13((right+left) / (right-left));

		m.setA22(2d*near / (top-bottom));
		m.setA23((top+bottom) / (top-bottom));

		m.setA33(-(far+near) / (far-near));
		m.setA34(-2d*far*near / (far-near));

		m.setA43(-1d);
		m.setA44(0d);

		return m;
	}

	public static Matrix4 createOrthographicProjectionMatrix(double left, double right, double top, double bottom, double near, double far) {

		Matrix4 m = new Matrix4();

		m.setA11(2d*near / (right-left));
		m.setA13((right+left) / (right-left));

		m.setA22(2d*near / (top-bottom));
		m.setA23((top+bottom) / (top-bottom));

		m.setA33(-(far+near) / (far-near));
		m.setA34(-2d*far*near / (far-near));

		m.setA43(-1d);
		m.setA44(0d);

		return m;
	}
}
