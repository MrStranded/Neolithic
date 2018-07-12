package engine.objects;

import math.Matrix4;
import math.Vector3;
import math.utils.MatrixTransformations;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderer.data.Mesh;

public class GraphicalObject {

	private Vector3 position = new Vector3(0,0,0);
	private Vector3 scale = new Vector3(1,1,1);
	private Vector3 rotation = new Vector3(0,0,0);

	private Matrix4 worldMatrix = new Matrix4();

	private Mesh mesh;

	public GraphicalObject(Mesh mesh) {

		this.mesh = mesh;
	}

	// ###################################################################################
	// ################################ Position #########################################
	// ###################################################################################

	public void translate(double x, double y, double z) {

		Vector3 v = new Vector3(x,y,z);
		position.plusInplace(v);
		worldMatrix = MatrixTransformations.translate(v).times(worldMatrix);
	}

	public void setPosition(double x, double y, double z) {

		position = new Vector3(x,y,z);
		updateWorldMatrix();
	}

	// ###################################################################################
	// ################################ Scale ############################################
	// ###################################################################################

	public void scale(double x, double y, double z) {

		Vector3 v = new Vector3(x,y,z);
		scale.timesElementwiseInplace(v);
		worldMatrix = MatrixTransformations.scale(v).times(worldMatrix);
	}

	public void setScale(double x, double y, double z) {

		scale = new Vector3(x,y,z);
		updateWorldMatrix();
	}

	// ###################################################################################
	// ################################ Rotation #########################################
	// ###################################################################################

	public void rotate(double x, double y, double z) {

		Vector3 v = new Vector3(x,y,z);
		rotation.plusInplace(v);
		worldMatrix = MatrixTransformations.rotate(v).times(worldMatrix);
	}

	public void rotateX(double a) {

		rotation.plusInplace(new Vector3(a,0,0));
		worldMatrix = MatrixTransformations.rotateX(a).times(worldMatrix);
	}
	public void rotateY(double a) {

		rotation.plusInplace(new Vector3(0,a,0));
		worldMatrix = MatrixTransformations.rotateY(a).times(worldMatrix);
	}
	public void rotateZ(double a) {

		rotation.plusInplace(new Vector3(0,0,a));
		worldMatrix = MatrixTransformations.rotateZ(a).times(worldMatrix);
	}

	public void setRotation(double x, double y, double z) {

		rotation = new Vector3(x,y,z);
		updateWorldMatrix();
	}

	// ###################################################################################
	// ################################ Update ###########################################
	// ###################################################################################

	private void updateWorldMatrix() {

		worldMatrix =   MatrixTransformations.translate(position).times(
						MatrixTransformations.rotate(rotation).times(
						MatrixTransformations.scale(scale)));
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

		// Draw the mesh
		GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

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

	public Mesh getMesh() {
		return mesh;
	}

	public Matrix4 getWorldMatrix() {
		return worldMatrix;
	}
}
