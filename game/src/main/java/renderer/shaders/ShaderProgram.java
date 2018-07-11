package renderer.shaders;

import org.lwjgl.opengl.GL20;

public class ShaderProgram {

	private final int programId;
	private int vertexShaderId;
	private int fragmentShaderId;

	public ShaderProgram() throws Exception {

		programId = GL20.glCreateProgram();
		if (programId == 0) {
			throw new Exception("Could not create shader.");
		}
	}

	// ###################################################################################
	// ################################ Set Up ###########################################
	// ###################################################################################

	public void createVertexShader(String shaderCode) throws Exception {
		vertexShaderId = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
	}

	public void createFragmentShader(String shaderCode) throws Exception {
		fragmentShaderId = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
	}

	private int createShader(String shaderCode, int shaderType) throws Exception {

		int shaderId = GL20.glCreateShader(shaderType);
		if (shaderId == 0) {
			throw new Exception("Error while creating shader of type " + shaderType);
		}

		GL20.glShaderSource(shaderId, shaderCode);
		GL20.glCompileShader(shaderId);

		if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
			throw new Exception("Error while compiling shader code: " + GL20.glGetShaderInfoLog(shaderId, 1024));
		}

		GL20.glAttachShader(programId, shaderId);

		// Setup Vertex Attributes ( https://stackoverflow.com/questions/21354301/glsl-syntax-problems-unexpected-new-identifier )
		//GL20.glBindAttribLocation(programId, 0, "location");

		return shaderId;
	}

	public void link() throws Exception {

		GL20.glLinkProgram(programId);
		if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
			throw new Exception("Error while linking Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
		}

		if (vertexShaderId != 0) {
			GL20.glDetachShader(programId, vertexShaderId);
		}
		if (fragmentShaderId != 0) {
			GL20.glDetachShader(programId, fragmentShaderId);
		}

		// TODO: Remove validation for finished version of game
		GL20.glValidateProgram(programId);
		if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0) {
			// not lethal and thus does not throw an Exception
			System.err.println("Warning validating Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
		}
	}

	public void bind() {
		GL20.glUseProgram(programId);
	}

	// ###################################################################################
	// ################################ Clean Up #########################################
	// ###################################################################################

	public void unbind() {
		GL20.glUseProgram(0);
	}

	public void cleanup() {
		unbind();
		if (programId != 0) {
			GL20.glDeleteProgram(programId);
		}
	}

}
