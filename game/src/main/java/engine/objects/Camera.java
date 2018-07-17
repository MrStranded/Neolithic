package engine.objects;

import engine.objects.movement.MoveableCamera;
import math.Matrix4;
import math.Vector3;

public class Camera extends MoveableCamera {

	public Camera() {
	}

	// ###################################################################################
	// ################################ Update ###########################################
	// ###################################################################################

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public Matrix4 getViewMatrix() {
		return matrix;
	}

	public Vector3 getPosition() {
		return new Vector3(0,0,radius);
	}

	public Vector3 getRotation() {
		return new Vector3(pitch+tilt,yaw,0);
	}
}
