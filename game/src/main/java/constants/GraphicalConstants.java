package constants;

public class GraphicalConstants {

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Rendering
	public static final double ZNEAR = 0.001d; // near viewing distance of camera
	public static final double ZFAR = 4000d; // far viewing distance of camera

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Shadow Map
	public static final int SHADOWMAP_SIZE = 2048; // width and height of shadow map in pixels
	public static final double SHADOWMAP_SCALE_FACTOR = 1.25; // scale factor of shadowMap.scale for orthographic projection

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Object Counts
	public static final int MAX_POINT_LIGHTS = 8; // maximum number of point lights in a scene
	public static final int MAX_SPOT_LIGHTS = 8; // maximum number of spot lights in a scene

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Composite Meshes
	public static final int DEFAULT_COMPOSITE_MESH_SIZE = 4; // default number of sub composite meshes of a composite mesh
	public static final int COMPOSITE_MESH_SIZE_CHANGE = 4; // enlargment or reduction of array of sub composite meshes

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Planetary LOD Adjustment
	public static final double PLANETARY_LOD_MATRIX_TILT_FACTOR = 0.5d; // angle multiplication factor for camera planetary matrices

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Planet Construction
	public static final double PLANET_CONSTRUCTION_SIDE_NORMAL_QUOTIENT = 16d; // inverse percentage of top normal that is added to side normals
}
