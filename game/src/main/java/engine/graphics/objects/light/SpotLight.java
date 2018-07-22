package engine.graphics.objects.light;

import engine.graphics.objects.movement.MoveableObject;
import engine.graphics.renderer.color.RGBA;
import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;
import engine.math.numericalObjects.Vector4;

public class SpotLight extends MoveableObject {

	private RGBA color;
	private float intensity = 1f;
	private Attenuation attenuation;

	private Vector3 viewPosition;
	private Vector3 direction;
	private Vector3 viewDirection;
	private double coneAngle = Math.PI/2;

	public SpotLight(RGBA color) {
		this.color = color;
		attenuation = Attenuation.CONSTANT();
		direction = new Vector3(0,0,1);
	}

	public SpotLight(double r, double g, double b) {
		this.color = new RGBA(r,g,b);
		attenuation = Attenuation.CONSTANT();
		direction = new Vector3(0,0,1);
	}

	// ###################################################################################
	// ################################ Math #############################################
	// ###################################################################################

	/**
	 * Calculates a Vector3 from the origin to the light source (this) and normalizes it.
	 * @param origin point from where we want to look into the direction of the light
	 * @return vector from origin to light with length 1
	 */
	public Vector3 toLight(Vector3 origin) {
		return position.minus(origin).normalize();
	}

	// ###################################################################################
	// ################################ Runtime Methods ##################################
	// ###################################################################################

	public void actualize(Matrix4 viewMatrix) {
		viewPosition = viewMatrix.times(getWorldMatrix().times(new Vector4(0,0,0,1))).extractVector3();
		viewDirection = viewMatrix.times(getWorldMatrix().times(new Vector4(direction,0))).extractVector3();
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

	public double getConeAngle() {
		return coneAngle;
	}
	public void setConeAngle(double coneAngle) {
		this.coneAngle = coneAngle;
	}

	public double getConeCosine() {
		return Math.cos(coneAngle);
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

	public Attenuation getAttenuation() {
		return attenuation;
	}
	public void setAttenuation(Attenuation attenuation) {
		this.attenuation = attenuation;
	}

	public Vector3 getPosition() {
		return position;
	}

	public Vector3 getRotation() {
		return rotation;
	}
	
	public Vector3 getViewPosition() {
		return viewPosition;
	}

	public Vector3 getViewDirection() {
		return viewDirection;
	}
}
