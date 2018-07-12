package engine.objects;

import math.Matrix4;
import math.Vector3;
import math.utils.MatrixTransformations;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderer.shapes.Mesh;

public class GraphicalObject {

	private Vector3 position = new Vector3(0,0,0);
	private Vector3 scale = new Vector3(1,1,1);
	private Vector3 rotation = new Vector3(0,0,0);

	private Matrix4 worldMatrix = new Matrix4();

	private Mesh mesh;

	public GraphicalObject(Mesh mesh) {

		this.mesh = mesh;
	}

	public void translate(double x, double y, double z) {

		Vector3 v = new Vector3(x,y,z);
		position.plusInplace(v);
		worldMatrix = MatrixTransformations.translate(v).times(worldMatrix);
	}

	public void setPosition(double x, double y, double z) {

		position = new Vector3(x,y,z);
		updateWorldMatrix();
	}

	public void scale(double x, double y, double z) {

		Vector3 v = new Vector3(x,y,z);
		scale.timesElementwiseInplace(v);
		worldMatrix = MatrixTransformations.scale(v).times(worldMatrix);
	}

	public void setScale(double x, double y, double z) {

		scale = new Vector3(x,y,z);
		updateWorldMatrix();
	}

	private void updateWorldMatrix() {

		worldMatrix =   MatrixTransformations.translate(position).times(
						MatrixTransformations.rotate(rotation).times(
						MatrixTransformations.scale(scale)));
	}

	public void render() {

		// Bind to the VAO
		GL30.glBindVertexArray(mesh.getVertexArrayObjectId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		// Draw the mesh
		GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

		// Restore state
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}

	public void cleanUp() {
		mesh.cleanUp();
	}

	public Mesh getMesh() {
		return mesh;
	}

	public Matrix4 getWorldMatrix() {
		return worldMatrix;
	}
}
