package engine.data.proto;

import constants.PropertyKeys;
import constants.ScriptConstants;
import constants.TopologyConstants;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.graphics.renderer.color.RGBA;

public class TileContainer extends Container {

	// tile specific
//	private int preferredHeight = 0;
//	private int preferredHeightBlur = 0;
//
//	private RGBA topColor = TopologyConstants.TILE_DEFAULT_COLOR;
//	private RGBA topColorDeviation = new RGBA(0,0,0);
//	private RGBA sideColor = TopologyConstants.TILE_DEFAULT_COLOR.times(TopologyConstants.TILE_SIDE_COLOR_FACTOR);
//	private RGBA sideColorDeviation = new RGBA(0,0,0);

	public TileContainer(String textID) {
		super(textID, DataType.TILE);
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public RGBA getTopColor() {
		return getProperty(null, PropertyKeys.TOP_COLOR.key()).map(Variable::getRGBA).orElse(new RGBA());
	}

	public RGBA getTopColorDeviation() {
		return getProperty(null, PropertyKeys.TOP_COLOR_DEVIATION.key()).map(Variable::getRGBA).orElse(new RGBA());
	}

	public RGBA getSideColor() {
		return getProperty(null, PropertyKeys.SIDE_COLOR.key()).map(Variable::getRGBA).orElse(new RGBA());
	}

	public RGBA getSideColorDeviation() {
		return getProperty(null, PropertyKeys.SIDE_COLOR_DEVIATION.key()).map(Variable::getRGBA).orElse(new RGBA());
	}

	public int getPreferredHeight() {
		return getProperty(ScriptConstants.DEFAULT_STAGE, PropertyKeys.PREFERRED_HEIGHT.key()).map(Variable::getInt).orElse(0);
	}

	public int getPreferredHeightBlur() {
		return getProperty(ScriptConstants.DEFAULT_STAGE, PropertyKeys.PREFERRED_HEIGHT_BLUR.key()).map(Variable::getInt).orElse(0);
	}
}
