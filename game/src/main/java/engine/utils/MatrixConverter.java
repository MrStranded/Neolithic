package engine.utils;

import math.Matrix4;

import java.nio.FloatBuffer;

public class MatrixConverter {

	public static void putMatrix4IntoFloatBuffer(Matrix4 m, FloatBuffer floatBuffer) {

		floatBuffer.put((float) m.getA11());
		floatBuffer.put((float) m.getA21());
		floatBuffer.put((float) m.getA31());
		floatBuffer.put((float) m.getA41());
		floatBuffer.put((float) m.getA12());
		floatBuffer.put((float) m.getA22());
		floatBuffer.put((float) m.getA32());
		floatBuffer.put((float) m.getA42());
		floatBuffer.put((float) m.getA13());
		floatBuffer.put((float) m.getA23());
		floatBuffer.put((float) m.getA33());
		floatBuffer.put((float) m.getA43());
		floatBuffer.put((float) m.getA14());
		floatBuffer.put((float) m.getA24());
		floatBuffer.put((float) m.getA34());
		floatBuffer.put((float) m.getA44());

		floatBuffer.flip();
	}
}
