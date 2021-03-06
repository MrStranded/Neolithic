package engine.graphics.objects;

import engine.graphics.objects.movement.MoveableCamera;
import engine.math.numericalObjects.Vector3;

public class Camera extends MoveableCamera {

	public Camera() {
	}

	// ###################################################################################
	// ################################ Update ###########################################
	// ###################################################################################

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public Vector3 getPosition() {
		return new Vector3(0,0,radius);
	}

	public Vector3 getRotation() {
		return new Vector3(pitch+tilt,yaw,0);
	}

	public double getRadius() {
		return radius;
	}
}
