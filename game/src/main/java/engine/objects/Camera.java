package engine.objects;

import math.Matrix4;
import math.Vector3;
import math.Vector4;
import math.utils.MatrixTransformations;

public class Camera extends MoveableObject {

	public Camera() {
	}

	// ###################################################################################
	// ################################ Update ###########################################
	// ###################################################################################

	@Override
	protected void optionalUpdate() {
		update();
	}

	@Override
	protected void update() {

		checkAngle();

		matrix =    MatrixTransformations.translate(position.times(-1d)).times(
					MatrixTransformations.rotate(rotation.times(-1d)));
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public Matrix4 getViewMatrix() {
		return matrix;
	}

	public Vector3 getPosition() {
		return position;
	}

	public Vector3 getRotation() {
		return rotation;
	}
}
