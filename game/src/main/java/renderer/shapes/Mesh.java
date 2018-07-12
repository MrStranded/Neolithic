package renderer.shapes;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {

	private final int vertexArrayObjectId; // Vertex Array Object Id
	private final int positionsVboId; // Positions Vertex Buffer Object Id
	private final int indicesVboId; // Indices Vertex Buffer Object Id
	private final int colorsVboId; // Colors Vertex Buffer Object Id

	private final int vertexCount;

	private float[] vertices;
	private int[] indices;
	private float[] colors;

	public Mesh(float[] vertices, int[] indices, float[] colors) {

		this.vertices = vertices;
		this.indices = indices;
		this.colors = colors;

		// set up static data

		// ------------------ whole object
		vertexCount = indices.length;

		vertexArrayObjectId = GL30.glGenVertexArrays();

		// ------------------ vertex part
		positionsVboId = GL15.glGenBuffers();

		// ------------------ index part
		indicesVboId = GL15.glGenBuffers();

		// ------------------ color part
		colorsVboId = GL15.glGenBuffers();

		// register mesh data
		registerData();
	}

	public void registerData() {

		FloatBuffer verticesBuffer = null;
		IntBuffer indicesBuffer = null;
		FloatBuffer colorsBuffer = null;

		try {

			// ------------------ whole object
			GL30.glBindVertexArray(vertexArrayObjectId);

			// ------------------ vertex part
			verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
			verticesBuffer.put(vertices).flip();

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionsVboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);

			GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

			// ------------------ index part
			indicesBuffer = MemoryUtil.memAllocInt(indices.length);
			indicesBuffer.put(indices).flip();

			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesVboId);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);

			// ------------------ color part
			colorsBuffer = MemoryUtil.memAllocFloat(colors.length);
			colorsBuffer.put(colors).flip();

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorsVboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorsBuffer, GL15.GL_STATIC_DRAW);

			GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0);

			// ------------------ unbind
			// Unbind the VBO
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			//GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			// Unbind the VAO
			GL30.glBindVertexArray(0);
		} finally {

			// ------------------ buffer clean up
			if (verticesBuffer != null) {
				MemoryUtil.memFree(verticesBuffer);
			}
			if (indicesBuffer != null) {
				MemoryUtil.memFree(indicesBuffer);
			}
			if (colorsBuffer != null) {
				MemoryUtil.memFree(colorsBuffer);
			}
		}
	}

	// ###################################################################################
	// ################################ Clean Up #########################################
	// ###################################################################################

	public void cleanUp() {

		GL20.glDisableVertexAttribArray(0);

		// Delete the VBOs
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(positionsVboId);
		GL15.glDeleteBuffers(indicesVboId);
		GL15.glDeleteBuffers(colorsVboId);

		// Delete the VAO
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vertexArrayObjectId);
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public void setZValues(float z) {

		for (int i=2; i<vertices.length; i+=3) {
			vertices[i] = z;
		}
	}

	public int getVertexArrayObjectId() {
		return vertexArrayObjectId;
	}

	public int getVertexBufferObjectId() {
		return positionsVboId;
	}

	public int getVertexCount() {
		return vertexCount;
	}
}
