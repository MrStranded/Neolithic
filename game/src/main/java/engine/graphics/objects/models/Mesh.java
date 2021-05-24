package engine.graphics.objects.models;

import engine.graphics.renderer.color.RGBA;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {

	private final int vertexArrayObjectId; // Vertex Array Object Id
	private final int indicesVboId; // Indices Vertex Buffer Object Id
	private final int verticesVboId;
	private final int normalsVboId;
	private final int textureCoordinatesVboId;
	private final int colorsVboId;

	private Material material; // the Material, containing also the texture
	//private RGBA color; // color of the mesh

	private final int vertexCount;

	private final int[] indices;
	private final float[] vertices;
	private final float[] normals;
	private final float[] textureCoordinates;
	private final float[] colors;

	public Mesh(int[] indices, float[] vertices, float[] textureCoordinates, float[] colors, float[] normals) {
		this.indices = indices;
		this.vertices = vertices;
		this.textureCoordinates = textureCoordinates;
		this.colors = colors;
		this.normals = normals;

		vertexCount = indices.length;

		material = new Material();
		//color = new RGBA(1,1,1,1);

		// set up static data

		// ------------------------------------ whole object
		vertexArrayObjectId = GL30.glGenVertexArrays();

		// ------------------------------------ index part
		indicesVboId = GL15.glGenBuffers();
		// ------------------------------------ attributes part
		verticesVboId = GL15.glGenBuffers();
		normalsVboId = GL15.glGenBuffers();
		textureCoordinatesVboId = GL15.glGenBuffers();
		colorsVboId = GL15.glGenBuffers();

		// register mesh data
		registerData();
	}

	public void registerData() {
		bind();

		loadIndexBuffer();
		loadDataBuffers();

		unbind();
	}

	private void bind() {
		GL30.glBindVertexArray(vertexArrayObjectId);
	}

	private void unbind() {
		// Unbind the VAO
		GL30.glBindVertexArray(0);
		// Unbind the VBO
//		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
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
			freeBuffer(indicesBuffer);
		}
	}

	private void loadDataBuffers() {
		bindBuffer(0, verticesVboId, vertices, 3);
		bindBuffer(1, textureCoordinatesVboId, textureCoordinates, 2);
		bindBuffer(2, colorsVboId, colors, 4);
		bindBuffer(3, normalsVboId, normals, 3);

		setAttributeFormat(0,3);
		setAttributeFormat(1,2);
		setAttributeFormat(2,4);
		setAttributeFormat(3,3);
	}

	private void bindBuffer(int bindingIndex, int bufferVboId, float[] data, int size) {
		FloatBuffer dataBuffer = null;

		try {
			dataBuffer = MemoryUtil.memAllocFloat(data.length);
			dataBuffer.put(data).flip();

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferVboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, dataBuffer, GL15.GL_STATIC_DRAW);

			GL43.glBindVertexBuffer(bindingIndex, bufferVboId, 0, size * 4);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			freeBuffer(dataBuffer);
		}
	}

	private void setAttributeFormat(int attributeIndex, int size) {
		GL43.glVertexAttribFormat(attributeIndex, size, GL11.GL_FLOAT, false, 0);
		GL43.glVertexAttribBinding(attributeIndex, attributeIndex);
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
		prepareRender();

		if (!useDepthTest) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		}

		pureRender();

		if (!useDepthTest) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}

		postRender();
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

	public void renderForShadowMap() {
		// Bind to the VAO
		GL30.glBindVertexArray(vertexArrayObjectId);
		GL20.glEnableVertexAttribArray(0);

		// Draw the mesh
		GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);

		// Restore state
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

	public void renderForGUI() {
		// Activate first texture unit
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		// Bind the texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, material.hasTexture() ? material.getTexture().getTextureId() : 0);

		// Bind to the VAO
		GL30.glBindVertexArray(vertexArrayObjectId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		// Draw the mesh
		GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);

		// Restore state
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	// ###################################################################################
	// ################################ Modification #####################################
	// ###################################################################################

	/**
	 * This method ensures that the x and y coordinates of the vertices reach (at most) from (0,0) to (1,1).
	 */
	public void normalize() {
		float leftMost=0f, rightMost=0f, topMost=0f, bottomMost=0f;

		// find out the dimensions of the mesh
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
		GL20.glDisableVertexAttribArray(3);

		// Delete the VBOs
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		GL15.glDeleteBuffers(verticesVboId);
		GL15.glDeleteBuffers(normalsVboId);
		GL15.glDeleteBuffers(textureCoordinatesVboId);
		GL15.glDeleteBuffers(colorsVboId);

		GL15.glDeleteBuffers(indicesVboId);

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
		for (int i = 0; i < colors.length / 4; i++) {
			colors[i*4 + 0] = r;
			colors[i*4 + 1] = g;
			colors[i*4 + 2] = b;
			colors[i*4 + 3] = a;
		}
		registerData();
	}

	public void setAlpha(float a) {
		for (int i = 0; i < colors.length / 4; i++) {
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
