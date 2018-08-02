package engine.graphics.renderer;

import constants.GraphicalConstants;
import constants.ResourcePathConstants;
import engine.data.Planet;
import engine.graphics.gui.GUIInterface;
import engine.graphics.objects.*;
import engine.graphics.objects.gui.GUIObject;
import engine.graphics.objects.light.*;
import engine.graphics.objects.planet.PlanetObject;
import engine.graphics.objects.textures.Texture;
import engine.graphics.renderer.projection.Projection;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.input.KeyboardInput;
import engine.input.MouseInput;
import engine.graphics.gui.window.Window;
import engine.math.numericalObjects.Vector3;
import load.StringLoader;
import engine.math.numericalObjects.Matrix4;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import static java.lang.System.exit;

/**
 * The renderer is only concerned about periodically drawing the given mesh data onto a window
 */

public class Renderer {

	private Window window;

	private ShaderProgram shaderProgram;
	private ShaderProgram hudShaderProgram;
	private ShaderProgram depthShaderProgram;

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
		initializeDepthShaders();

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

	private void initializeDepthShaders() {
		// loading and linking the shaders
		try {
			depthShaderProgram = new ShaderProgram();
			depthShaderProgram.createVertexShader(StringLoader.read(ResourcePathConstants.DEPTH_VERTEX_SHADER));
			depthShaderProgram.createFragmentShader(StringLoader.read(ResourcePathConstants.DEPTH_FRAGMENT_SHADER));
			depthShaderProgram.link();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void initializeUniforms() {
		try {
			// ---------------------------------------------------------- world scene
			shaderProgram.createUniform("modelViewMatrix");
			shaderProgram.createUniform("projectionMatrix");
			shaderProgram.createUniform("modelLightViewMatrix");
			shaderProgram.createUniform("lightProjectionMatrix");

			shaderProgram.createUniform("textureSampler");
			shaderProgram.createUniform("shadowSampler");

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

			// ---------------------------------------------------------- shadow maps
			depthShaderProgram.createUniform("orthographicProjectionMatrix");
			depthShaderProgram.createUniform("modelLightViewMatrix");
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
		double angleStep = 0;//0.0025d;
		angle += angleStep;
		if (angle > Math.PI*2d) {
			angle -= Math.PI*2d;
		}

		GraphicalObject[] objects = scene.getObjects();
		Camera camera = scene.getCamera();

		objects[2].rotateYAroundOrigin(-angleStep);
		objects[3].rotateYAroundOrigin(-angleStep);
		camera.rotateYaw(-angleStep);
		scene.getDirectionalLight().rotateY(-angleStep);
		scene.getPointLights()[1].rotateYAroundOrigin(-angleStep);
		//scene.getShadowMaps()[0].setDirection(new Vector3(-Math.sin(angle), 0, -Math.cos(angle)));
		scene.getShadowMap().setLightAngle(-angle);
		scene.getShadowMap().setCameraAngle(-camera.getRotation().getY());
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
			planetObject = new PlanetObject(new Planet(32));
			//planetObject.setScale(3,3,3);
		} else {
			//planetObject.rotateY(0.001d);
		}

		// Render depth map before view ports has been set up
		if (scene.getShadowMap() != null) {
			renderDepthMap(scene.getShadowMap(), scene, hud);
		}

		GL11.glViewport(0, 0, window.getWidth(), window.getHeight());

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
			//cleanUp(); // somehow enabling this here causes the program to not close properly anymore
			window.close();
		}
	}

	private void renderDepthMap(ShadowMap shadowMap, Scene scene, GUIInterface hud) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
		GL11.glViewport(0, 0, GraphicalConstants.SHADOWMAP_SIZE, GraphicalConstants.SHADOWMAP_SIZE);

		GL11.glClear(/*GL11.GL_COLOR_BUFFER_BIT | */GL11.GL_DEPTH_BUFFER_BIT);

		depthShaderProgram.bind();

		Matrix4 viewMatrix = shadowMap.getViewMatrix();
		Matrix4 orthographicProjectionMatrix = shadowMap.getOrthographicProjection();
		depthShaderProgram.setUniform("orthographicProjectionMatrix", orthographicProjectionMatrix);

		for (GraphicalObject object : scene.getObjects()) {
			if (object != null) {
				depthShaderProgram.setUniform("modelLightViewMatrix", viewMatrix.times(object.getWorldMatrix()));

				object.render();
			}
		}

		if (planetObject != null) {
			depthShaderProgram.setUniform("modelLightViewMatrix", viewMatrix.times(planetObject.getWorldMatrix()));

			// here we pass the shaderProgram because in FacePart.render() we need set some uniforms
			planetObject.render(depthShaderProgram, scene.getCamera().getPlanetaryLODMatrix(), false);
		}

		depthShaderProgram.unbind();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

		hud.getHUDObjects()[0].getMesh().getMaterial().setTexture(shadowMap.getDepthMap());
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
		// set shadow matrices
		ShadowMap shadowMap = scene.getShadowMap();
		if (shadowMap != null) {
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowMap.getDepthMap() != null ? shadowMap.getDepthMap().getTextureId() : 0);

			shaderProgram.setUniform("shadowSampler", 1);
			shaderProgram.setUniform("lightProjectionMatrix", shadowMap.getOrthographicProjection());
		}

		Matrix4 viewMatrix = camera.getViewMatrix();

		for (GraphicalObject object : scene.getObjects()) {
			if (object != null) {
				shaderProgram.setUniform("modelViewMatrix", viewMatrix.times(object.getWorldMatrix()));
				shaderProgram.setUniform("affectedByLight", object.isAffectedByLight() ? 1 : 0);
				shaderProgram.setUniform("dynamic", object.isStatic() ? 0 : 1);
				shaderProgram.setUniform("color", object.getMesh().getColor());
				shaderProgram.setUniform("material", object.getMesh().getMaterial());

				if (shadowMap != null) {
					shaderProgram.setUniform("modelLightViewMatrix", shadowMap.getViewMatrix().times(object.getWorldMatrix()));
				}

				object.render();
			}
		}

		if (planetObject != null) {
			shaderProgram.setUniform("modelViewMatrix", viewMatrix.times(planetObject.getWorldMatrix()));
			shaderProgram.setUniform("affectedByLight", 1);
			shaderProgram.setUniform("dynamic", 1);

			if (shadowMap != null) {
				shaderProgram.setUniform("modelLightViewMatrix", shadowMap.getViewMatrix().times(planetObject.getWorldMatrix()));
			}

			// here we pass the shaderProgram because in FacePart.render() we need set some uniforms
			planetObject.render(shaderProgram, camera.getPlanetaryLODMatrix(), true);
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
				hudShaderProgram.setUniform("color", object.getMesh().getColor());
				object.render();
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
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public double getAspectRatio() {
		return (double) window.getWidth() / (double) window.getHeight();
	}
}