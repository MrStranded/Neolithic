package engine.graphics.renderer.shaders;

import engine.graphics.objects.light.Attenuation;
import engine.graphics.objects.light.DirectionalLight;
import engine.graphics.objects.light.PointLight;
import engine.graphics.objects.light.SpotLight;
import engine.graphics.objects.models.Material;
import engine.graphics.renderer.color.RGBA;
import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;
import engine.utils.converters.MatrixConverter;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class ShaderProgram {

	private final int programId;
	private int vertexShaderId;
	private int fragmentShaderId;

	FloatBuffer matrixFloatBuffer;

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
			throw new Exception("Error while creating shader '" + shaderCode + "' of type " + shaderType);
		}

		GL20.glShaderSource(shaderId, shaderCode);
		GL20.glCompileShader(shaderId);

		if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
			throw new Exception("Error while compiling shader. Code: " + GL20.glGetShaderInfoLog(shaderId, 1024));
		}

		GL20.glAttachShader(programId, shaderId);

		return shaderId;
	}

	public void linkNormal() throws Exception {
		GL20.glBindAttribLocation(programId, 0, "inPosition");
		GL20.glBindAttribLocation(programId, 1, "inTextureCoordinates");
		GL20.glBindAttribLocation(programId, 2, "inColor");
		GL20.glBindAttribLocation(programId, 3, "inNormal");

		link();
	}

	public void linkDepth() throws Exception {
		GL20.glBindAttribLocation(programId, 0, "inPosition");

		link();
	}

	public void linkHUD() throws Exception {
		GL20.glBindAttribLocation(programId, 0, "inPosition");
		GL20.glBindAttribLocation(programId, 1, "inTextureCoordinates");
		GL20.glBindAttribLocation(programId, 2, "inColor");

		link();
	}

	private void link() throws Exception {
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
	// ################################ Generation #######################################
	// ###################################################################################

	public void createDirectionalLightUniform(String uniformName) throws Exception {
		createUniform(uniformName + ".color");
		createUniform(uniformName + ".intensity");
		createUniform(uniformName + ".direction");
	}

	public void createPointLightUniform(String uniformName) throws Exception {
		createUniform(uniformName + ".color");
		createUniform(uniformName + ".position");
		createUniform(uniformName + ".intensity");
		createUniform(uniformName + ".attenuation.constant");
		createUniform(uniformName + ".attenuation.linear");
		createUniform(uniformName + ".attenuation.exponent");
	}

	public void createSpotLightUniform(String uniformName) throws Exception {
		createUniform(uniformName + ".color");
		createUniform(uniformName + ".position");
		createUniform(uniformName + ".intensity");
		createUniform(uniformName + ".direction");
		createUniform(uniformName + ".coneCosine");
		createUniform(uniformName + ".attenuation.constant");
		createUniform(uniformName + ".attenuation.linear");
		createUniform(uniformName + ".attenuation.exponent");
	}

	public void createMaterialUniform(String uniformName) throws Exception {
		createUniform(uniformName + ".ambient");
		createUniform(uniformName + ".diffuse");
		createUniform(uniformName + ".reflectance");
		createUniform(uniformName + ".specularPower");
		createUniform(uniformName + ".hasTexture");
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
		if (matrixFloatBuffer == null) {
			// Dump the matrix into a float buffer
			try (MemoryStack stack = MemoryStack.stackPush()) {
				matrixFloatBuffer = stack.mallocFloat(16);
			}
		}

		MatrixConverter.putMatrix4IntoFloatBuffer(m, matrixFloatBuffer);
		GL20.glUniformMatrix4fv(uniforms.get(uniformName), false, matrixFloatBuffer);
	}

	public void setUniform(String uniformName, RGBA color) {
		GL20.glUniform4f(uniforms.get(uniformName), (float) color.getR(), (float) color.getG(), (float) color.getB(), (float) color.getA());
	}

	public void setUniform(String uniformName, Vector3 vector) {
		GL20.glUniform3f(uniforms.get(uniformName), (float) vector.getX(), (float) vector.getY(), (float) vector.getZ());
	}

	public void setUniform(String uniformName, int value) {
		GL20.glUniform1i(uniforms.get(uniformName), value);
	}

	public void setUniform(String uniformName, float value) {
		GL20.glUniform1f(uniforms.get(uniformName), value);
	}

	public void setUniform(String uniformName, Material material) {
		setUniform(uniformName + ".ambient", material.getAmbientStrength());
		setUniform(uniformName + ".diffuse", material.getDiffuseStrength());
		setUniform(uniformName + ".reflectance", material.getReflectanceStrength());

		setUniform(uniformName + ".specularPower", material.getSpecularPower());

		setUniform(uniformName + ".hasTexture", material.hasTexture() ? 1 : 0);
	}

	public void setUniform(String uniformName, DirectionalLight directionalLight) {
		setUniform(uniformName + ".color", directionalLight.getColor());
		setUniform(uniformName + ".intensity", directionalLight.getIntensity());
		setUniform(uniformName + ".direction", directionalLight.getViewDirection());
	}

	public void setUniform(String uniformName, PointLight pointLight, int pos) {
		setUniform(uniformName + "[" + pos + "]", pointLight);
	}

	public void setUniform(String uniformName, PointLight pointLight) {
		setUniform(uniformName + ".color", pointLight.getColor());
		setUniform(uniformName + ".position", pointLight.getViewPosition());
		setUniform(uniformName + ".intensity", pointLight.getIntensity());

		Attenuation attenuation = pointLight.getAttenuation();
		setUniform(uniformName + ".attenuation.constant", attenuation.getConstant());
		setUniform(uniformName + ".attenuation.linear", attenuation.getLinear());
		setUniform(uniformName + ".attenuation.exponent", attenuation.getExponent());
	}

	public void setUniform(String uniformName, PointLight[] pointLights) {
		int numberOfLights = pointLights != null? pointLights.length : 0;
		for (int i=0; i<numberOfLights; i++) {
			if (pointLights[i] != null) {
				setUniform(uniformName, pointLights[i], i);
			}
		}
	}

	public void setUniform(String uniformName, SpotLight spotLight, int pos) {
		setUniform(uniformName + "[" + pos + "]", spotLight);
	}

	public void setUniform(String uniformName, SpotLight spotLight) {
		setUniform(uniformName + ".color", spotLight.getColor());
		setUniform(uniformName + ".position", spotLight.getViewPosition());
		setUniform(uniformName + ".intensity", spotLight.getIntensity());
		setUniform(uniformName + ".direction", spotLight.getViewDirection());
		setUniform(uniformName + ".coneCosine", (float) spotLight.getConeCosine());

		Attenuation attenuation = spotLight.getAttenuation();
		setUniform(uniformName + ".attenuation.constant", attenuation.getConstant());
		setUniform(uniformName + ".attenuation.linear", attenuation.getLinear());
		setUniform(uniformName + ".attenuation.exponent", attenuation.getExponent());
	}

	public void setUniform(String uniformName, SpotLight[] spotLights) {
		int numberOfLights = spotLights != null? spotLights.length : 0;
		for (int i=0; i<numberOfLights; i++) {
			if (spotLights[i] != null) {
				setUniform(uniformName, spotLights[i], i);
			}
		}
	}
}
