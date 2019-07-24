package constants;

import engine.graphics.renderer.color.RGBA;

public class TopologyConstants {

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Planet %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	public static final double PLANET_MINIMUM_HEIGHT = 1024d; // this value corresponds to the radius of the planet and tile heights are 'added' to this
	public static final double PLANET_MAXIMUM_HEIGHT = 256d; // the maximum height value a tile can have
	public static final int PLANET_OZEAN_HEIGHT = 128; // the default height of water on new topology

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Default Colors %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	public static final float WATER_ALPHA = 1f; // alpha value of water material
	public static final RGBA TILE_DEFAULT_COLOR = new RGBA(1d, 0.75d, 0.5d, 1d); // default color of new topology
	public static final RGBA WATER_DEFAULT_COLOR = new RGBA(0.25d, 0.5d, 0.75d, WATER_ALPHA); // default color of water
	public static final double TILE_SIDE_COLOR_FACTOR = 0.75d; // if no side color is available, the top color is multiplied with this factor to create a side color
}
