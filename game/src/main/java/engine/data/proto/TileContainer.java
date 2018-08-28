package engine.data.proto;

import constants.TopologyConstants;
import engine.data.variables.DataType;
import engine.graphics.renderer.color.RGBA;

public class TileContainer extends Container {

	// tile specific
	private int preferredHeight = 0;
	private int preferredHeightBlur = 0;

	private RGBA color = TopologyConstants.TILE_DEFAULT_COLOR;
	private RGBA colorDeviation = new RGBA(0,0,0);

	public TileContainer(String textID) {
		super(textID, DataType.TILE);
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

	public RGBA getColorDeviation() {
		return colorDeviation;
	}
	public void setColorDeviation(RGBA colorDeviation) {
		this.colorDeviation = colorDeviation;
	}

	public int getPreferredHeight() {
		return preferredHeight;
	}
	public void setPreferredHeight(int preferredHeight) {
		this.preferredHeight = preferredHeight;
	}

	public int getPreferredHeightBlur() {
		return preferredHeightBlur;
	}
	public void setPreferredHeightBlur(int preferredHeightBlur) {
		this.preferredHeightBlur = preferredHeightBlur;
	}
}
