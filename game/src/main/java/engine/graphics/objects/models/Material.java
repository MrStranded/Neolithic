package engine.graphics.objects.models;

import engine.graphics.renderer.color.RGBA;

public class Material {

	private RGBA ambientStrength;
	private RGBA diffuseStrength;
	private RGBA reflectanceStrength;

	public Material() {

		ambientStrength = new RGBA(1,1,1,1);
		diffuseStrength = new RGBA(1,1,1,1);
		reflectanceStrength = new RGBA(1,1,1,1);
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public RGBA getAmbientStrength() {
		return ambientStrength;
	}
	public void setAmbientStrength(RGBA ambientStrength) {
		this.ambientStrength = ambientStrength;
	}

	public RGBA getDiffuseStrength() {
		return diffuseStrength;
	}
	public void setDiffuseStrength(RGBA diffuseStrength) {
		this.diffuseStrength = diffuseStrength;
	}

	public RGBA getReflectanceStrength() {
		return reflectanceStrength;
	}
	public void setReflectanceStrength(RGBA reflectanceStrength) {
		this.reflectanceStrength = reflectanceStrength;
	}
}
