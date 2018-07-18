package engine.graphics.objects.light;

import engine.graphics.renderer.color.RGBA;

public class AmbientLight {

	private RGBA color;

	public AmbientLight(RGBA color) {
		this.color = color;
	}

	public AmbientLight(double r, double g, double b) {
		this.color = new RGBA(r,g,b);
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
