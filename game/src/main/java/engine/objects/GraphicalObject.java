package engine.objects;

import engine.objects.movement.MoveableObject;
import math.Matrix4;
import math.Vector3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import engine.renderer.data.Mesh;

public class GraphicalObject extends MoveableObject {

	private Mesh mesh;
	private boolean useDepthTest = true;

	public GraphicalObject(Mesh mesh) {

		this.mesh = mesh;
	}

	// ###################################################################################
	// ################################ Rendering ########################################
	// ###################################################################################

	public void render() {

		// Activate first texture unit
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		// Bind the texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getTextureId());

		// Bind to the VAO
		GL30.glBindVertexArray(mesh.getVertexArrayObjectId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		if (!useDepthTest) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		}

		// Draw the mesh
		GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

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
		mesh.cleanUp();
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public void setColor(float r, float g, float b) {
		mesh.setColor(r,g,b);
	}

	public Mesh getMesh() {
		return mesh;
	}

	public Matrix4 getWorldMatrix() {
		return matrix;
	}

	public Vector3 getPosition() {
		return position;
	}

	public Vector3 getScale() {
		return scale;
	}

	public Vector3 getRotation() {
		return rotation;
	}

	public boolean isUseDepthTest() {
		return useDepthTest;
	}
	public void setUseDepthTest(boolean useDepthTest) {
		this.useDepthTest = useDepthTest;
	}
}
