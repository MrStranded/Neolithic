package engine.data.attributes;

/**
 * PreAttributes are created while loading the game data.
 * It may happen, that we encounter a textID for an attribute that we cannot yet resolve.
 */
public class PreAttribute {

	private String textID;
	private int value;
	private int variation;

	public PreAttribute(String textID, int value, int variation) {
		this.textID = textID;
		this.value = value;
		this.variation = variation;
	}

	public String getTextID() {
		return textID;
	}

	public int getValue() {
		return value;
	}

	public int getVariation() {
		return variation;
	}

}
