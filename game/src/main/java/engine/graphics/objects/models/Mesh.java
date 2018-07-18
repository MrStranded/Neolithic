package engine.graphics.objects.models;

import engine.graphics.renderer.color.RGBA;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {

	private final int vertexArrayObjectId; // Vertex Array Object Id
	private final int positionsVboId; // Positions Vertex Buffer Object Id
	private final int indicesVboId; // Indices Vertex Buffer Object Id
	private final int normalsVboId; // Colors Vertex Buffer Object Id
	private final int textureId; // Texture Buffer Id
	private final int textureVboId; // Texture Coordinates Vertex Buffer Object Id

	private Material material; // the Material, containing also the texture
	private RGBA color; // color of the mesh

	private final int vertexCount;

	private float[] vertices;
	private int[] indices;
	private float[] normals;
	private float[] textureCoordinates;

	public Mesh(float[] vertices, int[] indices, float[] normals, float[] textureCoordinates) {

		this.vertices = vertices;
		this.indices = indices;
		this.normals = normals;
		this.textureCoordinates = textureCoordinates;

		material = new Material();
		color = new RGBA(1,1,1,1);

		// set up static data

		// ------------------ whole object
		vertexCount = indices.length;

		vertexArrayObjectId = GL30.glGenVertexArrays();

		// ------------------ vertex part
		positionsVboId = GL15.glGenBuffers();

		// ------------------ index part
		indicesVboId = GL15.glGenBuffers();

		// ------------------ color part
		normalsVboId = GL15.glGenBuffers();

		// ------------------ texture part
		textureId = GL11.glGenTextures();
		textureVboId = GL15.glGenBuffers();

		// register mesh data
		registerData();
	}

	public void registerData() {

		FloatBuffer verticesBuffer = null;
		IntBuffer indicesBuffer = null;
		FloatBuffer normalsBuffer = null;
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

			// ------------------ normal part
			normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
			normalsBuffer.put(normals).flip();

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalsVboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normalsBuffer, GL15.GL_STATIC_DRAW);

			GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0);

			// ------------------ texture part
			if (material.hasTexture()) {
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
				GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,
						material.getTexture().getWidth(),
						material.getTexture().getHeight(),
						0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
						material.getTexture().getBuffer());
				GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

				textureBuffer = MemoryUtil.memAllocFloat(textureCoordinates.length);
				textureBuffer.put(textureCoordinates).flip();

				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textureVboId);
				GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textureBuffer, GL15.GL_STATIC_DRAW);

				GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0);
			}

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
			if (normalsBuffer != null) {
				MemoryUtil.memFree(normalsBuffer);
			}
		}
	}

	// ###################################################################################
	// ################################ Render ###########################################
	// ###################################################################################

	public void render(boolean useDepthTest) {

		// Activate first texture unit
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		// Bind the texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

		// Bind to the VAO
		GL30.glBindVertexArray(vertexArrayObjectId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		if (!useDepthTest) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		}

		// Draw the mesh
		GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);

		if (!useDepthTest) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}

		// Restore state
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
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
		GL15.glDeleteBuffers(normalsVboId);

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
		registerData();
	}

	public void setColor(float r, float g, float b) {
		color = new RGBA(r,g,b);
	}
	public void setColor(float r, float g, float b, float a) {
		color = new RGBA(r,g,b,a);
	}
	public RGBA getColor() {
		return color;
	}

	public void setTexture(Texture texture) {
		material.setTexture(texture);
		registerData();
	}
	public Texture getTexture() {
		return material.getTexture();
	}
	public boolean hasTexture() {
		return material.hasTexture();
	}

	public Material getMaterial() {
		return material;
	}
	public void setMaterial(Material material) {
		this.material = material;
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
