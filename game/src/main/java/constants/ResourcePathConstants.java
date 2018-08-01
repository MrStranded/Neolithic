package constants;

/**
 * Constants for resource paths. The paths always end with a file separator "/" if it is for a folder.
 */
public class ResourcePathConstants {

	public static final String RESOURCE_FOLDER          = "src/main/resources/";

	public static final String SHADER_FOLDER            = RESOURCE_FOLDER + "shaders/";
	public static final String WORLD_VERTEX_SHADER      = SHADER_FOLDER + "vertex.vs";
	public static final String WORLD_FRAGMENT_SHADER    = SHADER_FOLDER + "fragment.fs";
	public static final String HUD_VERTEX_SHADER        = SHADER_FOLDER + "orthoVertex.vs";
	public static final String HUD_FRAGMENT_SHADER      = SHADER_FOLDER + "orthoFragment.fs";
	public static final String DEPTH_VERTEX_SHADER      = SHADER_FOLDER + "depthVertex.vs";
	public static final String DEPTH_FRAGMENT_SHADER    = SHADER_FOLDER + "depthFragment.fs";

	public static final String FONT_FOLDER              = RESOURCE_FOLDER + "fonts/";
}
