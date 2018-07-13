package engine.renderer.shaders;

import math.Matrix4;
import math.utils.MatrixConverter;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class ShaderProgram {

	private final int programId;
	private int vertexShaderId;
	private int fragmentShaderId;

	private final Map<String, Integer> uniforms;

	public ShaderProgram() throws Exception {

		programId = GL20.glCreateProgram();
		if (programId == 0) {
			throw new Exception("Could not create shader.");
		}

		uniforms = new HashMap<>();
	}

	// ###################################################################################
	// ################################ Set Up ###########################################
	// ###################################################################################

	public void createUniform(String uniformName) throws Exception {
		int uniformLocation = GL20.glGetUniformLocation(programId, uniformName);
		if (uniformLocation < 0) {
			throw new Exception("Could not find uniform: " + uniformName);
		}
		uniforms.put(uniformName, uniformLocation);
	}

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

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public void setUniform(String uniformName, Matrix4 m) {
		// Dump the matrix into a float buffer
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer floatBuffer = stack.mallocFloat(16);
			MatrixConverter.putMatrix4IntoFloatBuffer(m, floatBuffer);
			GL20.glUniformMatrix4fv(uniforms.get(uniformName), false, floatBuffer);
		}
	}

	public void setUniform(String uniformName, int value) {
		GL20.glUniform1i(uniforms.get(uniformName), value);
	}

}