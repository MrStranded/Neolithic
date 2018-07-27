package engine.graphics.objects.textures;

public class CharInfo {

	private final int xPos;
	private final int width;

	public CharInfo(int xPos, int width) {
		this.xPos = xPos;
		this.width = width;
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public int getxPos() {
		return xPos;
	}

	public int getWidth() {
		return width;
	}
}
