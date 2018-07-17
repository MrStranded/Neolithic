package engine.utils;

import engine.renderer.color.RGBA;

import java.nio.FloatBuffer;

public class ColorConverter {

	public static void putColorIntoFloatBuffer(RGBA color, FloatBuffer floatBuffer) {

		floatBuffer.put((float) color.getR());
		floatBuffer.put((float) color.getG());
		floatBuffer.put((float) color.getB());
		floatBuffer.put((float) color.getA());

		floatBuffer.flip();
	}
}
