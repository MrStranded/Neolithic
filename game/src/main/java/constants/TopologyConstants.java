package constants;

import engine.graphics.renderer.color.RGBA;

public class TopologyConstants {

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Planet %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	public static final double PLANET_MINIMUM_HEIGHT = 1024d; // this value corresponds to the radius of the planet and tile heights are 'added' to this
	public static final double PLANET_MAXIMUM_HEIGHT = 256d; // the maximum height value a tile can have
	public static final int PLANET_OZEAN_HEIGHT = 150; // the default height of water on new tiles

	public static final RGBA TILE_DEFAULT_COLOR = new RGBA(1d, 0.75d, 0.5d, 1d);
}
