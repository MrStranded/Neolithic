package constants;

public class GraphicalConstants {

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Rendering
	public static final double ZNEAR = 0.001d;
	public static final double ZFAR = 4000d;

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Shadow Map
	public static final int SHADOWMAP_SIZE = 1024;

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Object Counts
	public static final int MAX_POINT_LIGHTS = 8;
	public static final int MAX_SPOT_LIGHTS = 8;

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Composite Meshes
	public static final int DEFAULT_COMPOSITE_MESH_SIZE = 4;
	public static final int COMPOSITE_MESH_SIZE_CHANGE = 4;

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Planetary LOD Adjustment
	public static final double PLANETARY_LOD_MATRIX_TILT_FACTOR = 0.5d;

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Planet Construction
	public static final double PLANET_CONSTRUCTION_SIDE_NORMAL_QUOTIENT = 16d;
}
