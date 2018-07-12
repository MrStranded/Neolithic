package renderer.data;

import org.lwjgl.system.MemoryUtil;

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

		FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(vertices.length);
		floatBuffer.put(vertices).flip();
		return floatBuffer;
	}

}
