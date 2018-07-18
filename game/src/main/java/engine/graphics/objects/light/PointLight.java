package engine.graphics.objects.light;

import engine.graphics.objects.movement.MoveableObject;
import engine.graphics.renderer.color.RGBA;
import engine.math.numericalObjects.Vector3;

public class PointLight extends MoveableObject {

	private RGBA color;

	public PointLight(RGBA color) {
		this.color = color;
	}

	public PointLight(double r, double g, double b) {
		this.color = new RGBA(r,g,b);
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
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public RGBA getColor() {
		return color;
	}
	public void setColor(RGBA color) {
		this.color = color;
	}
}
