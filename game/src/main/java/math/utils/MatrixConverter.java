package math.utils;

import math.Matrix4;

import java.nio.FloatBuffer;

public class MatrixConverter {

	public static void putMatrix4IntoFloatBuffer(Matrix4 m, FloatBuffer floatBuffer) {

	    /*float[] floats = new float[16];
	    floats[0] = (float) m.getA11();
	    floats[1] = (float) m.getA12();
	    floats[2] = (float) m.getA13();
	    floats[3] = (float) m.getA14();
	    floats[4] = (float) m.getA21();
	    floats[5] = (float) m.getA22();
	    floats[6] = (float) m.getA23();
	    floats[7] = (float) m.getA24();
	    floats[8] = (float) m.getA31();
	    floats[9] = (float) m.getA32();
	    floats[10] = (float) m.getA33();
	    floats[11] = (float) m.getA34();
	    floats[12] = (float) m.getA41();
	    floats[13] = (float) m.getA42();
	    floats[14] = (float) m.getA43();
	    floats[15] = (float) m.getA44();
	    floatBuffer.put(floats);*/

		floatBuffer.put((float) m.getA11());
		floatBuffer.put((float) m.getA12());
		floatBuffer.put((float) m.getA13());
		floatBuffer.put((float) m.getA14());
		floatBuffer.put((float) m.getA21());
		floatBuffer.put((float) m.getA22());
		floatBuffer.put((float) m.getA23());
		floatBuffer.put((float) m.getA24());
		floatBuffer.put((float) m.getA31());
		floatBuffer.put((float) m.getA32());
		floatBuffer.put((float) m.getA33());
		floatBuffer.put((float) m.getA34());
		floatBuffer.put((float) m.getA41());
		floatBuffer.put((float) m.getA42());
		floatBuffer.put((float) m.getA43());
		floatBuffer.put((float) m.getA44());
		floatBuffer.flip();
	}
}
