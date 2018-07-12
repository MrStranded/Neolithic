package renderer.camera;

import math.Matrix4;
import math.Vector3;
import math.utils.MatrixTransformations;

public class Camera {

	private Vector3 position = new Vector3(0,0,0);
	private Vector3 rotation = new Vector3(0,0,0);

	private Matrix4 viewMatrix = new Matrix4();

	public Camera() {
	}

	public void translate(double x, double y, double z) {

		Vector3 v = new Vector3(-x,-y,-z);
		position.plusInplace(v);
		viewMatrix = MatrixTransformations.translate(v).times(viewMatrix);
	}

	public void setPosition(double x, double y, double z) {

		position = new Vector3(-x,-y,-z);
		updateViewMatrix();
	}

	public void rotate(double x, double y, double z) {

		Vector3 v = new Vector3(-x,-y,-z);
		rotation.plusInplace(v);
		viewMatrix = MatrixTransformations.rotate(v).times(viewMatrix);
	}

	public void setRotation(double x, double y, double z) {

		rotation = new Vector3(-x,-y,-z);
		updateViewMatrix();
	}

	private void updateViewMatrix() {
		viewMatrix = MatrixTransformations.translate(position).times(MatrixTransformations.rotate(rotation));
	}

	public Matrix4 getViewMatrix() {
		return viewMatrix;
	}
}
