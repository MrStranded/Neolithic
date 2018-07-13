package engine.renderer.data;

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
	private final int textureId; // Texture Buffer Id
	private final int textureVboId; // Texture Coordinates Vertex Buffer Object Id

	private Texture texture; // the Texture itself

	private final int vertexCount;

	private float[] vertices;
	private int[] indices;
	private float[] colors;
	private float[] textureCoordinates;

	public Mesh(float[] vertices, int[] indices, float[] colors, Texture texture, float[] textureCoordinates) {

		this.vertices = vertices;
		this.indices = indices;
		this.colors = colors;
		this.textureCoordinates = textureCoordinates;

		this.texture = texture;

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

		// ------------------ texture part
		textureId = GL11.glGenTextures();
		textureVboId = GL15.glGenBuffers();

		// register mesh data
		registerData();
	}

	public void registerData() {

		FloatBuffer verticesBuffer = null;
		IntBuffer indicesBuffer = null;
		FloatBuffer colorsBuffer = null;
		FloatBuffer textureBuffer = null;

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

			// ------------------ texture part
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, texture.getWidth(), texture.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, texture.getBuffer());
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

			textureBuffer = MemoryUtil.memAllocFloat(textureCoordinates.length);
			textureBuffer.put(textureCoordinates).flip();

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textureVboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textureBuffer, GL15.GL_STATIC_DRAW);

			GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0);

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

	public int getTextureId() {
		return textureId;
	}

	public int getVertexCount() {
		return vertexCount;
	}
}
