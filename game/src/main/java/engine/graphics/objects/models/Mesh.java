package engine.graphics.objects.models;

import engine.graphics.renderer.color.RGBA;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {

	private final int vertexArrayObjectId; // Vertex Array Object Id
	private final int positionsVboId; // Positions Vertex Buffer Object Id
	private final int indicesVboId; // Indices Vertex Buffer Object Id
	private final int normalsVboId; // Colors Vertex Buffer Object Id
	private final int textureVboId; // Texture Coordinates Vertex Buffer Object Id
	private final int colorVboId; // Color Vertex Buffer Object Id

	private Material material; // the Material, containing also the texture
	//private RGBA color; // color of the mesh

	private final int vertexCount;

	private float[] vertices;
	private int[] indices;
	private float[] normals;
	private float[] textureCoordinates;
	private float[] colors;

	public Mesh(float[] vertices, int[] indices, float[] normals, float[] textureCoordinates, float[] colors) {
		this.vertices = vertices;
		this.indices = indices;
		this.normals = normals;
		this.textureCoordinates = textureCoordinates;
		this.colors = colors;

		material = new Material();
		//color = new RGBA(1,1,1,1);

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

		// ------------------ color part
		colorVboId = GL15.glGenBuffers();

		// register mesh data
		registerData();
	}

	public void registerData() {
		// ------------------ whole object
		bind();

		// ------------------ index part
		loadIndexBuffer();

		// ------------------ vertex part
		loadDataBuffer(vertices, positionsVboId, 0, 3);

		// ------------------ normal part
		loadDataBuffer(normals, normalsVboId, 1, 3);

		// ------------------ texture part
		loadDataBuffer(textureCoordinates, textureVboId, 2, 2);

		// ------------------ color part
		loadDataBuffer(colors, colorVboId, 3, 4);

		// ------------------ unbind
		unbind();
	}

	private void bind() {
		GL30.glBindVertexArray(vertexArrayObjectId);
	}

	private void unbind() {
		// Unbind the VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		// Unbind the VAO
		GL30.glBindVertexArray(0);
	}

	private void loadIndexBuffer() {
		IntBuffer indicesBuffer = null;

		try {
			indicesBuffer = MemoryUtil.memAllocInt(indices.length);
			indicesBuffer.put(indices).flip();

			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesVboId);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			// ------------------ buffer clean up
			freeBuffer(indicesBuffer);
		}
	}

	private void loadDataBuffer(float[] data, int bufferVboId, int index, int size) {
		FloatBuffer dataBuffer = null;

		try {
			dataBuffer = MemoryUtil.memAllocFloat(data.length);
			dataBuffer.put(data).flip();

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferVboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, dataBuffer, GL15.GL_STATIC_DRAW);

			GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			// ------------------ buffer clean up
			freeBuffer(dataBuffer);
		}
	}

	private void freeBuffer(Buffer buffer) {
		if (buffer != null) {
			MemoryUtil.memFree(buffer);
		}
	}

	// ###################################################################################
	// ################################ Render ###########################################
	// ###################################################################################

	/**
	 * Use this method to render single meshes that are not part of a MeshHub.
	 * In a situation where you want to render multiple objects with the same mesh use the methods:
	 * prepareRender(), pureRender() and postRender().
	 * @param useDepthTest whether to use depth test or not
	 */
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
		GL20.glEnableVertexAttribArray(3);

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
		GL20.glDisableVertexAttribArray(3);
		GL30.glBindVertexArray(0);
	}

	/**
	 * Preparation method to draw multiple objects with the same mesh.
	 * Used in MeshHub.render().
	 */
	public void prepareRender() {
		// Activate first texture unit
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		// Bind the texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, material.hasTexture() ? material.getTexture().getTextureId() : 0);

		// Bind to the VAO
		GL30.glBindVertexArray(vertexArrayObjectId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
	}

	/**
	 * Method to draw multiple objects with the same mesh.
	 * Used in MeshHub.render().
	 */
	public void pureRender() {
		// Draw the mesh
		GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);
	}

	/**
	 * Clean up method after rendering multiple objects with the same mesh.
	 * Used in MeshHub.render().
	 */
	public void postRender() {
		// Restore state
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
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
		for (float vertex : vertices) {
			if (dimension == 0) { // X dimension
				if (vertex < leftMost) {
					leftMost = vertex;
				}
				if (vertex > rightMost) {
					rightMost = vertex;
				}
			} else if (dimension == 1) { // Y dimension
				if (vertex < bottomMost) {
					bottomMost = vertex;
				}
				if (vertex > topMost) {
					topMost = vertex;
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
		setColor((float) color.getR(), (float) color.getG(), (float) color.getB(), (float) color.getA());
	}
	public void setColor(float r, float g, float b) {
		setColor(r, g, b, 1);
	}
	public void setColor(float r, float g, float b, float a) {
		for (int i=0; i<colors.length/4; i++) {
			colors[i*4 + 0] = r;
			colors[i*4 + 1] = g;
			colors[i*4 + 2] = b;
			colors[i*4 + 3] = a;
		}
		registerData();
	}

	public Material getMaterial() {
		return material;
	}
	public void setMaterial(Material material) {
		this.material = material;
	}

	public float[] getVertices() {
		return vertices;
	}
	public int[] getIndices() {
		return indices;
	}
	public float[] getNormals() {
		return normals;
	}
	public float[] getTextureCoordinates() {
		return textureCoordinates;
	}
	public float[] getColors() {
		return colors;
	}
}
