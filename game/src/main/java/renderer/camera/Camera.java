package renderer.camera;

import math.Vector3;

public class Camera {

	private Vector3 position;
	private double viewPlaneZPosition;

	public Camera() {
		position = new Vector3(0,0,-10);
		viewPlaneZPosition = -5;
	}

	public Camera(Vector3 position, double viewPlaneZPosition) {
		this.position = position;
		this.viewPlaneZPosition = viewPlaneZPosition;
	}

}
