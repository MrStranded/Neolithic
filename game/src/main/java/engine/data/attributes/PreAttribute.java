package engine.data.attributes;

/**
 * PreAttributes are created while loading the game data.
 * It may happen, that we encounter a textID for an attribute that we cannot yet resolve.
 */
public class PreAttribute {

	private String textID;
	private String stage;
	private int value;
	private double variation;
	private double variationProbability;

	public PreAttribute(String textID, String stage, int value, double variation, double variationProbability) {
		this.textID = textID;
		this.stage = stage;
		this.value = value;
		this.variation = variation;
		this.variationProbability = variationProbability;
	}

	public String getTextID() {
		return textID;
	}
	public String getStage() { return stage; }
	public int getValue() {
		return value;
	}
	public double getVariation() {
		return variation;
	}
	public double getVariationProbability() { return variationProbability; }

}
