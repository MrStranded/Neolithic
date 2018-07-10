package renderer.shapes;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class Triangle {

	public float[] vertices;

	public Triangle() {

		vertices = new float[]{
				0.0f,  0.5f, 0.0f,
				-0.5f, -0.5f, 0.0f,
				0.5f, -0.5f, 0.0f
		};
	}

	public FloatBuffer getFloatBuffer() {

		FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(vertices.length * 3);
		floatBuffer.put(vertices).flip();
		return floatBuffer;
	}

}
