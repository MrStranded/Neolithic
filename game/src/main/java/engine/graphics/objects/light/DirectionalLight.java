package engine.graphics.objects.light;

import engine.graphics.objects.movement.MoveableObject;
import engine.graphics.renderer.color.RGBA;
import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;
import engine.math.numericalObjects.Vector4;

public class DirectionalLight extends MoveableObject {

	private RGBA color;
	private float intensity = 1f;

	private Vector3 direction;
	private Vector3 viewDirection;

	public DirectionalLight(RGBA color) {
		this.color = color;
		direction = new Vector3(0,0,1);
	}

	public DirectionalLight(double r, double g, double b) {
		this.color = new RGBA(r,g,b);
		direction = new Vector3(0,0,1);
	}

	// ###################################################################################
	// ################################ Math #############################################
	// ###################################################################################

	// ###################################################################################
	// ################################ Runtime Methods ##################################
	// ###################################################################################

	public void actualize(Matrix4 viewMatrix) {
		viewDirection = viewMatrix.times(getWorldMatrix()).times(new Vector4(direction,0)).extractVector3();
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public Vector3 getDirection() {
		return direction;
	}
	public void setDirection(Vector3 direction) {
		this.direction = direction;
	}

	public RGBA getColor() {
		return color;
	}
	public void setColor(RGBA color) {
		this.color = color;
	}

	public float getIntensity() {
		return intensity;
	}
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public Vector3 getRotation() {
		return rotation;
	}

	public Vector3 getViewDirection() {
		return viewDirection;
	}
}
