package renderer.shapes;

import math.Vector3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import utils.VectorConverter;

import java.nio.FloatBuffer;

public class Mesh {

	private final int vertexArrayObjectId;
	private final int vertexBufferObjectId;

	private final int vertexCount;

	public Mesh(Vector3[] positions) {

		FloatBuffer verticesBuffer = null;

		try {

			verticesBuffer = MemoryUtil.memAllocFloat(positions.length*3);
			vertexCount = positions.length;
			verticesBuffer.put(VectorConverter.VectorArrayToFloatArray(positions)).flip();

			vertexArrayObjectId = GL30.glGenVertexArrays();
			GL30.glBindVertexArray(vertexArrayObjectId);

			vertexBufferObjectId = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferObjectId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);

			GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

			// Unbind the VBO
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

			// Unbind the VAO
			GL30.glBindVertexArray(0);
		} finally {

			if (verticesBuffer != null) {
				MemoryUtil.memFree(verticesBuffer);
			}
		}
	}

	// ###################################################################################
	// ################################ Clean Up #########################################
	// ###################################################################################

	public void cleanUp() {

		GL20.glDisableVertexAttribArray(0);

		// Delete the VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vertexBufferObjectId);

		// Delete the VAO
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vertexArrayObjectId);
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public int getVertexArrayObjectId() {
		return vertexArrayObjectId;
	}

	public int getVertexBufferObjectId() {
		return vertexBufferObjectId;
	}

	public int getVertexCount() {
		return vertexCount;
	}
}
