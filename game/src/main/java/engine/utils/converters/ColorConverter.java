package engine.utils.converters;

import engine.graphics.renderer.color.RGBA;

import java.nio.FloatBuffer;
import java.util.List;

public class ColorConverter {

	public static void putColorIntoFloatBuffer(RGBA color, FloatBuffer floatBuffer) {
		floatBuffer.put((float) color.getR());
		floatBuffer.put((float) color.getG());
		floatBuffer.put((float) color.getB());
		floatBuffer.put((float) color.getA());

		floatBuffer.flip();
	}

	public static float[] RGBAListToFloatArray(List<RGBA> colors) {
		float[] floats = new float[colors.size()*4];

		int i=0;
		for (RGBA color: colors) {
			floats[i++] = (float) color.getR();
			floats[i++] = (float) color.getG();
			floats[i++] = (float) color.getB();
			floats[i++] = (float) color.getA();
		}

		return floats;
	}
}
