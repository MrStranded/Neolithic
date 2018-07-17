package engine.graphics.objects.models;

/**
 * An IndexGroup holds the index values of a vertex. This concerns index of the vertex, textureCoordinates and normal elemnts in their respective lists.
 * An IndexGroup is initialized with default UNDEF values (-1), which will produce an ArrayIndexOutOfBoundsException or similar if misused (not set).
 */
public class IndexGroup {

	public static final int UNDEF = -1;

	private int positionIndex;
	private int textureCoordinatesIndex;
	private int normalIndex;

	public IndexGroup() {
		positionIndex = UNDEF;
		textureCoordinatesIndex = UNDEF;
		normalIndex = UNDEF;
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public int getPositionIndex() {
		return positionIndex;
	}
	public void setPositionIndex(int positionIndex) {
		this.positionIndex = positionIndex;
	}

	public int getTextureCoordinatesIndex() {
		return textureCoordinatesIndex;
	}
	public void setTextureCoordinatesIndex(int textureCoordinatesIndex) {
		this.textureCoordinatesIndex = textureCoordinatesIndex;
	}

	public int getNormalIndex() {
		return normalIndex;
	}
	public void setNormalIndex(int normalIndex) {
		this.normalIndex = normalIndex;
	}
}
