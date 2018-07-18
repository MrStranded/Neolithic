package engine.graphics.objects.light;

import engine.graphics.objects.movement.MoveableObject;
import engine.graphics.renderer.color.RGBA;
import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;
import engine.math.numericalObjects.Vector4;

public class PointLight extends MoveableObject {

	private RGBA color;
	private float intensity = 1f;
	private Attenuation attenuation;
	
	private Vector3 viewPosition;

	public PointLight(RGBA color) {
		this.color = color;
		attenuation = Attenuation.CONSTANT();
	}

	public PointLight(double r, double g, double b) {
		this.color = new RGBA(r,g,b);
		attenuation = Attenuation.CONSTANT();
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

	public void actualizeViewPosition(Matrix4 viewMatrix) {
		viewPosition = viewMatrix.times(matrix.times(new Vector4(position))).extractVector3();
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

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
	
	public Vector3 getViewPosition() {
		return viewPosition;
	}
}
