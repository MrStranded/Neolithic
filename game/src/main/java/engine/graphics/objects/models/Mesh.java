package engine.graphics.objects.models;

import engine.graphics.objects.textures.Texture;
import engine.graphics.renderer.color.RGBA;
import engine.graphics.renderer.shaders.ShaderProgram;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {

	private final int vertexArrayObjectId; // Vertex Array Object Id
	private final int positionsVboId; // Positions Vertex Buffer Object Id
	private final int indicesVboId; // Indices Vertex Buffer Object Id
	private final int normalsVboId; // Colors Vertex Buffer Object Id
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

		// ------------------ normal part
		normalsVboId = GL15.glGenBuffers();

		// ------------------ texture part
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

			// ------------------ index part
			indicesBuffer = MemoryUtil.memAllocInt(indices.length);
			indicesBuffer.put(indices).flip();

			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesVboId);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);

			// ------------------ vertex part
			verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
			verticesBuffer.put(vertices).flip();

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionsVboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);

			GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

			// ------------------ normal part
			normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
			normalsBuffer.put(normals).flip();

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalsVboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normalsBuffer, GL15.GL_STATIC_DRAW);

			GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0);

			// ------------------ texture part
			textureBuffer = MemoryUtil.memAllocFloat(textureCoordinates.length);
			textureBuffer.put(textureCoordinates).flip();

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textureVboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textureBuffer, GL15.GL_STATIC_DRAW);

			GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0);

			// ------------------ unbind
			// Unbind the VBO
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
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
			if (textureBuffer != null) {
				MemoryUtil.memFree(textureBuffer);
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
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, material.hasTexture() ? material.getTexture().getTextureId() : 0);

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
	// ################################ Modification #####################################
	// ###################################################################################

	public void setZValues(float z) {
		for (int i=2; i<vertices.length; i+=3) {
			vertices[i] = z;
		}
		registerData();
	}

	public void randomizeTextureCoordinates() {
		for (int i=0; i<textureCoordinates.length; i++) {
			textureCoordinates[i] = (float) Math.random();
		}
		registerData();
	}

	/**
	 * This method ensures that the x and y coordinates of the vertices reach (at most) from (0,0) to (1,1).
	 */
	public void normalize() {
		float leftMost=0f, rightMost=0f, topMost=0f, bottomMost=0f;

		// find out the dimenstions of the mesh
		int dimension = 0;
		for (int i=0; i<vertices.length; i++) {
			if (dimension == 0) { // X dimension
				if (vertices[i] < leftMost) {
					leftMost = vertices[i];
				}
				if (vertices[i] > rightMost) {
					rightMost = vertices[i];
				}
			} else if (dimension == 1) { // Y dimension
				if (vertices[i] < bottomMost) {
					bottomMost = vertices[i];
				}
				if (vertices[i] > topMost) {
					topMost = vertices[i];
				}
			}

			dimension = (dimension + 1) % 3;
		}

		float width = rightMost - leftMost;
		float height = topMost - bottomMost;
		if (width == 0f) { width = 1f; }
		if (height == 0f) { height = 1f; }

		// modify the coordinates to fit into the desired space from (0,0) to (1,1)
		dimension = 0;
		for (int i=0; i<vertices.length; i++) {
			if (dimension == 0) { // X dimension
				vertices[i] = (vertices[i] - leftMost) / width;
			} else if (dimension == 1) { // Y dimension
				vertices[i] = (vertices[i] - bottomMost) / height;
			}

			dimension = (dimension + 1) % 3;
		}

		registerData();
	}

	// ###################################################################################
	// ################################ Clean Up #########################################
	// ###################################################################################

	public void cleanUp() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);

		// Delete the VBOs
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(positionsVboId);
		GL15.glDeleteBuffers(indicesVboId);
		GL15.glDeleteBuffers(normalsVboId);
		GL15.glDeleteBuffers(textureVboId);

		// Delete the VAO
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vertexArrayObjectId);
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public void setColor(RGBA color) {
		this.color = color;
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

	public Material getMaterial() {
		return material;
	}
	public void setMaterial(Material material) {
		this.material = material;
	}
}
