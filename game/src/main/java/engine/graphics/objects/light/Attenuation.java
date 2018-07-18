package engine.graphics.objects.light;

/**
 * Attenuation. How the intensity of the light changes with distance. The strength is divided by the following value:
 * float attenuationInverse =
 * light.attenuation.constant +
 * light.attenuation.linear * distance +
 * light.attenuation.exponent * distance * distance;
 */
public class Attenuation {

	// a new undefined attenuation will not decrease the strength of the light by distance
	private float constant = 1;
	private float linear = 0;
	private float exponent = 0;

	public Attenuation(float constant, float linear, float exponent) {
		this.constant = constant;
		this.linear = linear;
		this.exponent = exponent;
	}

	// ###################################################################################
	// ################################ Generation #######################################
	// ###################################################################################

	public static Attenuation FAR() {
		return new Attenuation(
				0.5f,
				0.0001f,
				0f
		);
	}

	public static Attenuation MEDIUM() {
		return new Attenuation(
				1f,
				0.1f,
				0.1f
		);
	}

	public static Attenuation CLOSE() {
		return new Attenuation(
				1f,
				1f,
				1f
		);
	}

	public static Attenuation CONSTANT() {
		return new Attenuation(
				1f,
				0f,
				0f
		);
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public float getConstant() {
		return constant;
	}
	public void setConstant(float constant) {
		this.constant = constant;
	}

	public float getLinear() {
		return linear;
	}
	public void setLinear(float linear) {
		this.linear = linear;
	}

	public float getExponent() {
		return exponent;
	}
	public void setExponent(float exponent) {
		this.exponent = exponent;
	}
}
