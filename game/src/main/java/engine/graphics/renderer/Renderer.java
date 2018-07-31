package engine.graphics.renderer;

import constants.GraphicalConstants;
import constants.ResourcePathConstants;
import engine.data.Planet;
import engine.graphics.gui.GUIInterface;
import engine.graphics.objects.*;
import engine.graphics.objects.light.*;
import engine.graphics.objects.planet.PlanetObject;
import engine.graphics.renderer.projection.Projection;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.input.KeyboardInput;
import engine.input.MouseInput;
import engine.graphics.window.Window;
import load.StringLoader;
import engine.math.numericalObjects.Matrix4;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import static java.lang.System.exit;

/**
 * The renderer is only concerned about periodically drawing the given mesh data onto a window
 */

public class Renderer {

	private Window window;
	private ShaderProgram shaderProgram;
	private ShaderProgram hudShaderProgram;

	private Matrix4 projectionMatrix;
	private Matrix4 orthographicMatrix;

	/**
	 * Whenever the aspect ratio has changed, we might need to update the scale of the hud objects.
	 * This is done in renderGUI().
	 */
	private boolean aspectRatioHasChanged = true;

	private MouseInput mouse;
	private KeyboardInput keyboard;

	private double angle = 0;
	private PlanetObject planetObject;

	public Renderer(Window window) {
		this.window = window;
		window.setRenderer(this);
	}

	// ###################################################################################
	// ################################ Set Up ###########################################
	// ###################################################################################

	public void initialize() {
		window.initialize();

		initializeShaders();
		initializeHUDShaders();
		initializeUniforms();
		initializeInput();

		calculateProjectionMatrix();
		calculateOrthographicMatrix();
	}

	private void initializeShaders() {
		// loading and linking the shaders
		try {
			shaderProgram = new ShaderProgram();
			shaderProgram.createVertexShader(StringLoader.read(ResourcePathConstants.WORLD_VERTEX_SHADER));
			shaderProgram.createFragmentShader(StringLoader.read(ResourcePathConstants.WORLD_FRAGMENT_SHADER));
			shaderProgram.link();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initializeHUDShaders() {
		// loading and linking the shaders
		try {
			hudShaderProgram = new ShaderProgram();
			hudShaderProgram.createVertexShader(StringLoader.read(ResourcePathConstants.HUD_VERTEX_SHADER));
			hudShaderProgram.createFragmentShader(StringLoader.read(ResourcePathConstants.HUD_FRAGMENT_SHADER));
			hudShaderProgram.link();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void initializeUniforms() {
		try {
			// ---------------------------------------------------------- normal scene
			shaderProgram.createUniform("modelViewMatrix");
			shaderProgram.createUniform("projectionMatrix");
			shaderProgram.createUniform("textureSampler");
			shaderProgram.createUniform("color");
			shaderProgram.createUniform("affectedByLight");
			shaderProgram.createUniform("dynamic");
			shaderProgram.createMaterialUniform("material");

			shaderProgram.createUniform("ambientLight");
			shaderProgram.createDirectionalLightUniform("directionalLight");
			for (int i = 0; i<GraphicalConstants.MAX_POINT_LIGHTS; i++) {
				shaderProgram.createPointLightUniform("pointLight[" + i + "]");
			}
			for (int i = 0; i<GraphicalConstants.MAX_SPOT_LIGHTS; i++) {
				shaderProgram.createSpotLightUniform("spotLight[" + i + "]");
			}

			// ---------------------------------------------------------- hud
			hudShaderProgram.createUniform("projectionViewMatrix");
			hudShaderProgram.createUniform("textureSampler");
			hudShaderProgram.createUniform("color");
		} catch (Exception e) {
			e.printStackTrace();
			exit(1);
		}
	}

	private void initializeInput() {
		mouse = new MouseInput(window);
		keyboard = new KeyboardInput(window);
	}

	// ###################################################################################
	// ################################ Calculation ######################################
	// ###################################################################################

	public void recalculateAspectRatio() {
		calculateProjectionMatrix();
		calculateOrthographicMatrix();

		aspectRatioHasChanged = true;
	}

	private void calculateProjectionMatrix() {
		double aspectRatio = getAspectRatio();

		// values are multiplied with znear so the size of objects on the screen is independant from the znear value
		projectionMatrix = Projection.createPerspectiveProjectionMatrix(
				-aspectRatio*GraphicalConstants.ZNEAR,
				aspectRatio*GraphicalConstants.ZNEAR,
				GraphicalConstants.ZNEAR,
				-GraphicalConstants.ZNEAR,
				GraphicalConstants.ZNEAR,
				GraphicalConstants.ZFAR
		);
	}

	private void calculateOrthographicMatrix() {
		double aspectRatio = getAspectRatio();

		orthographicMatrix = Projection.createOrthographicProjectionMatrix(-aspectRatio, aspectRatio,1d,-1d,0d,10d);
	}

	// ###################################################################################
	// ################################ Input SHOULD NOT STAY HERE !!!! ##################
	// ###################################################################################

	private void processInput(Scene scene) {
		double angleStep = 0.0025d;
		angle += angleStep;
		if (angle > Math.PI*2d) {
			angle -= Math.PI*2d;
		}

		GraphicalObject[] objects = scene.getObjects();
		Camera camera = scene.getCamera();

		objects[2].rotateYAroundOrigin(-angleStep);
		objects[3].rotateYAroundOrigin(angleStep*2);
		scene.getDirectionalLight().rotateY(-angleStep);
		scene.getPointLights()[1].rotateYAroundOrigin(-angleStep);
		//scene.getSpotLights()[5].rotateYAroundOrigin(-angleStep);
		//spotLight.setDirection(spotLight.getPosition().times(-1).normalize());

		if (keyboard.isPressed(GLFW.GLFW_KEY_A)) { // rotate left
			camera.rotateYaw(-0.01d);
		}
		if (keyboard.isPressed(GLFW.GLFW_KEY_D)) { // rotate right
			camera.rotateYaw(0.01d);
		}
		if (keyboard.isPressed(GLFW.GLFW_KEY_E)) { // look down
			camera.rotateTilt(-0.01d);
		}
		if (keyboard.isPressed(GLFW.GLFW_KEY_Q)) { // look up
			camera.rotateTilt(0.01d);
		}
		if (keyboard.isPressed(GLFW.GLFW_KEY_W)) { // rotate up
			camera.rotatePitch(-0.01d);
		}
		if (keyboard.isPressed(GLFW.GLFW_KEY_S)) { // rotate down
			camera.rotatePitch(0.01d);
		}
		if (keyboard.isPressed(GLFW.GLFW_KEY_R)) { // go closer
			camera.changeRadius(-camera.getRadius()/100d);
		}
		if (keyboard.isPressed(GLFW.GLFW_KEY_F)) { // go farther away
			camera.changeRadius(camera.getRadius()/100d);
		}
	}

	// ###################################################################################
	// ################################ Rendering ########################################
	// ###################################################################################

	public void render(Scene scene, GUIInterface hud) {
		processInput(scene);
		if (planetObject == null) {
			planetObject = new PlanetObject(new Planet(64));
			planetObject.setScale(3,3,3);
		}

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		if (scene != null) {
			renderScene(scene);
		}
		if (hud != null) {
			renderGUI(hud);
		}

		flip();

		// closing window
		if (keyboard.isClicked(GLFW.GLFW_KEY_ESCAPE)) {
			//cleanUp(); // somehow enabling this causes the program to not close properly anymore
			window.close();
		}
	}

	private void renderScene(Scene scene) {
		shaderProgram.bind();

		Camera camera = scene.getCamera();

		// upload projection matrix
		shaderProgram.setUniform("projectionMatrix", projectionMatrix);
		// set used texture (id = 0)
		shaderProgram.setUniform("textureSampler", 0);
		// set directional light uniforms
		scene.getDirectionalLight().actualize(camera.getViewMatrix());
		shaderProgram.setUniform("directionalLight",scene.getDirectionalLight());
		// set point light uniforms
		for (PointLight pointLight : scene.getPointLights()) {
			if (pointLight != null) {
				pointLight.actualize(camera.getViewMatrix());
			}
		}
		shaderProgram.setUniform("pointLight",scene.getPointLights());
		// set spot light uniforms
		for (SpotLight spotLight : scene.getSpotLights()) {
			if (spotLight != null) {
				spotLight.actualize(camera.getViewMatrix());
			}
		}
		shaderProgram.setUniform("spotLight",scene.getSpotLights());
		// set ambient light
		shaderProgram.setUniform("ambientLight",scene.getAmbientLight().getColor());

		Matrix4 viewMatrix = camera.getViewMatrix();

		for (GraphicalObject object : scene.getObjects()) {
			if (object != null) {
				shaderProgram.setUniform("modelViewMatrix", viewMatrix.times(object.getWorldMatrix()));
				shaderProgram.setUniform("affectedByLight", object.isAffectedByLight() ? 1 : 0);
				shaderProgram.setUniform("dynamic", object.isStatic() ? 0 : 1);
				object.render(shaderProgram, true);
			}
		}

		if (planetObject != null) {
			shaderProgram.setUniform("modelViewMatrix", camera.getPlanetaryLODMatrix().times(planetObject.getWorldMatrix()));
			shaderProgram.setUniform("affectedByLight", 1);
			shaderProgram.setUniform("dynamic", 1);
			planetObject.render(shaderProgram, true, camera.getViewMatrix());
		}

		shaderProgram.unbind();
	}

	private void renderGUI(GUIInterface hud) {
		hudShaderProgram.bind();

		// set used texture (id = 0)
		hudShaderProgram.setUniform("textureSampler", 0);

		for (GUIObject object : hud.getHUDObjects()) {
			if (object != null) {
				if (aspectRatioHasChanged) {
					object.recalculateScale(window.getWidth(), window.getHeight());
				}

				hudShaderProgram.setUniform("projectionViewMatrix", orthographicMatrix.times(object.getWorldMatrix()));
				object.render(hudShaderProgram, false);
			}
		}

		hudShaderProgram.unbind();

		aspectRatioHasChanged = false;
	}

	// ###################################################################################
	// ################################ Runtime Methods ##################################
	// ###################################################################################

	public boolean displayExists() {
		return !window.isClosed();
	}

	public void flip() {
		window.flip();
	}

	// ###################################################################################
	// ################################ Clean Up #########################################
	// ###################################################################################

	public void cleanUp() {
		if (shaderProgram != null) {
			shaderProgram.cleanup();
		}
		if (hudShaderProgram != null) {
			hudShaderProgram.cleanup();
		}

		window.destroy();
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public double getAspectRatio() {
		return (double) window.getWidth() / (double) window.getHeight();
	}
}