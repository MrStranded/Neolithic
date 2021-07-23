package constants;

public class GraphicalConstants {

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Rendering %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	public static final double ZNEAR = 0.001d; // near viewing distance of camera
	public static final double ZFAR = 4000d; // far viewing distance of camera

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% GUI Rendering %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	public static final double GUI_ZNEAR = 0d; // near viewing distance of camera for gui
	public static final double GUI_ZFAR = 10d; // far viewing distance of camera for gui

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Shadow Map %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	public static final int SHADOWMAP_SIZE = 4096; // width and height of shadow map in pixels
	public static final double SHADOWMAP_SCALE_FACTOR = 1.25; // scale factor of shadowMap for orthographic projection
	public static final double SHADOWMAP_MIN_SCALING = 1d; // how much the shadowmap gets scaled at least when zooming out
	public static final double SHADOWMAP_MAX_SCALING = 12d; // the maximal scaling of the shadowmap when zooming in
	public static final double SHADOWMAP_BRIGHT_SPOT_SIZE = 8d; // the inverse multiplied with PI is the size in radian
	public static final double SHADOWMAP_MIN_EPSILON = 0.0001d; // the minimum epsilon (bias) value for distance checks when close
	public static final double SHADOWMAP_MAX_EPSILON = 0.005d; // the maximum epsilon (bias) value for distance checks when far away
	public static final double SHADOWMAP_RADIUS_SCOPE = 0.5d; // from distance to distance + scope, various values change based on camera radius
	public static final double SHADOWMAP_MIN_ZVALUE = 0.25d; // the minimal zNear and zFar values when close
	public static final double SHADOWMAP_MAX_ZVALUE = 1.0d; // the maximal zNear and zFar values when far away

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Object Counts %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	public static final int MAX_POINT_LIGHTS = 8; // maximum number of point lights in a scene
	public static final int MAX_SPOT_LIGHTS = 8; // maximum number of spot lights in a scene

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Composite Meshes %%%%%%%%%%%%%%%%%%%%%%%%%%%
	public static final int DEFAULT_COMPOSITE_MESH_SIZE = 4; // default number of sub composite meshes of a composite mesh
	public static final int COMPOSITE_MESH_SIZE_CHANGE = 4; // enlargment or reduction of array of sub composite meshes

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Planetary LOD Adjustment %%%%%%%%%%%%%%%%%%%
	public static final double PLANETARY_LOD_MATRIX_TILT_FACTOR = 0.25d; // angle multiplication factor for camera planetary matrices

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Planet Construction %%%%%%%%%%%%%%%%%%%%%%%%
	public static final double PLANET_CONSTRUCTION_SIDE_NORMAL_QUOTIENT = 16d; // inverse amount of top normal that is added to side normals

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Sun Properties %%%%%%%%%%%%%%%%%%%%%%%%
	public static final double SUN_DISTANCE = 1000d;

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Font Properties %%%%%%%%%%%%%%%%%%%%%%%%
	public static final String DEFAULT_FONT = "/fonts/Stonism/D3Stonism-GOdm.ttf";
	public static final String FALLBACK_FONT = "Arial";
	public static final int FONT_WIDTH_PADDING = 4;
	public static final int FONT_SIZE_FOR_TEXTURE = 100;
	public static final double DEFAULT_FONT_SIZE = 30.0;
}
