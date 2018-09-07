package engine.data.proto;

import constants.TopologyConstants;
import engine.data.variables.DataType;
import engine.graphics.renderer.color.RGBA;

public class TileContainer extends Container {

	// tile specific
	private int preferredHeight = 0;
	private int preferredHeightBlur = 0;

	private RGBA topColor = TopologyConstants.TILE_DEFAULT_COLOR;
	private RGBA topColorDeviation = new RGBA(0,0,0);
	private RGBA sideColor = TopologyConstants.TILE_DEFAULT_COLOR.times(TopologyConstants.TILE_SIDE_COLOR_FACTOR);
	private RGBA sideColorDeviation = new RGBA(0,0,0);

	public TileContainer(String textID) {
		super(textID, DataType.TILE);
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public RGBA getTopColor() {
		return topColor;
	}
	public void setTopColor(RGBA topColor) {
		this.topColor = topColor;
	}

	public RGBA getTopColorDeviation() {
		return topColorDeviation;
	}
	public void setTopColorDeviation(RGBA topColorDeviation) {
		this.topColorDeviation = topColorDeviation;
	}

	public RGBA getSideColor() {
		return sideColor;
	}
	public void setSideColor(RGBA sideColor) {
		this.sideColor = sideColor;
	}

	public RGBA getSideColorDeviation() {
		return sideColorDeviation;
	}
	public void setSideColorDeviation(RGBA sideColorDeviation) {
		this.sideColorDeviation = sideColorDeviation;
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
