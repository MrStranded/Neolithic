package engine.graphics.renderer;

import constants.GameConstants;
import constants.GraphicalConstants;
import constants.ResourcePathConstants;
import engine.TimedTask;
import engine.data.entities.Instance;
import engine.data.options.GameOptions;
import engine.data.planetary.Planet;
import engine.data.Data;
import engine.data.planetary.Tile;
import engine.data.proto.Container;
import engine.data.scripts.ScriptRun;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.graphics.gui.GUIInterface;
import engine.graphics.objects.*;
import engine.graphics.objects.generators.PlanetGenerator;
import engine.graphics.objects.gui.GUIObject;
import engine.graphics.objects.light.*;
import engine.graphics.objects.planet.PlanetObject;
import engine.graphics.renderer.projection.Projection;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.input.KeyboardInput;
import engine.input.MouseInput;
import engine.graphics.gui.window.Window;
import engine.logic.topology.TopologyGenerator;
import engine.math.MatrixCalculations;
import engine.math.MousePicking;
import load.StringLoader;
import engine.math.numericalObjects.Matrix4;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.util.*;

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
	private Matrix4 invertedProjectionMatrix;
	private Matrix4 orthographicMatrix;

	/**
	 * Whenever the aspect ratio has changed, we might need to update the scale of the hud objects.
	 * This is done in renderGUI().
	 */
	private boolean aspectRatioHasChanged = true;

	private MouseInput mouse;
	private KeyboardInput keyboard;

	private List<TimedTask> tasks = Collections.emptyList();

	private Scene scene = null;
	private GUIInterface hud = null;
	private Planet planet = null;
	private PlanetObject planetObject = null;

	private double angle = 0;

	public Renderer(Window window) {
		this.window = window;
		window.setRenderer(this);

		initialize();
	}

	// ###################################################################################
	// ################################ Set Up ###########################################
	// ###################################################################################

	public void initialize() {
		initializeShaders();
		initializeHUDShaders();
		initializeDepthShaders();

		initializeUniforms();
		initializeInput();

		calculateProjectionMatrizes();
		calculateOrthographicMatrix();

		initializeTasks();
	}

	private void initializeShaders() {
		try {
			shaderProgram = new ShaderProgram();
			shaderProgram.createVertexShader(StringLoader.read(ResourcePathConstants.WORLD_VERTEX_SHADER));
			shaderProgram.createFragmentShader(StringLoader.read(ResourcePathConstants.WORLD_FRAGMENT_SHADER));
			shaderProgram.linkNormal();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initializeHUDShaders() {
		try {
			hudShaderProgram = new ShaderProgram();
			hudShaderProgram.createVertexShader(StringLoader.read(ResourcePathConstants.HUD_VERTEX_SHADER));
			hudShaderProgram.createFragmentShader(StringLoader.read(ResourcePathConstants.HUD_FRAGMENT_SHADER));
			hudShaderProgram.linkHUD();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initializeDepthShaders() {
		try {
			depthShaderProgram = new ShaderProgram();
			depthShaderProgram.createVertexShader(StringLoader.read(ResourcePathConstants.DEPTH_VERTEX_SHADER));
			depthShaderProgram.createFragmentShader(StringLoader.read(ResourcePathConstants.DEPTH_FRAGMENT_SHADER));
			depthShaderProgram.linkDepth();
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
			shaderProgram.createUniform("shadowStrength");
			shaderProgram.createUniform("shadowEpsilon");

			//shaderProgram.createUniform("color");
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
			//hudShaderProgram.createUniform("color");

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

	private void initializeTasks() {
		tasks = TimedTask.listOf(
				new TimedTask("Rendering Shadow Map", () -> renderDepthMap(scene.getShadowMap()), 100),

				new TimedTask("Rendering Scene", () -> {
					GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
					GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

					renderScene();
				}, 100),

				new TimedTask("Rendering HUD", this::renderGUI, 100)
		);
	}

	// ###################################################################################
	// ################################ Calculation ######################################
	// ###################################################################################

	public void recalculateAspectRatio() {
		calculateProjectionMatrizes();
		calculateOrthographicMatrix();

		aspectRatioHasChanged = true;
	}

	private void calculateProjectionMatrizes() {
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

		// we need the inverted projection matrix to create intersection rays from the mouse position
		invertedProjectionMatrix = MatrixCalculations.invert(projectionMatrix);
	}

	private void calculateOrthographicMatrix() {
		double aspectRatio = getAspectRatio();

		orthographicMatrix = Projection.createOrthographicProjectionMatrix(
				-aspectRatio, aspectRatio,
				1d,-1d,
				GraphicalConstants.GUI_ZNEAR,
				GraphicalConstants.GUI_ZFAR
		);
	}

	// ###################################################################################
	// ################################ Input SHOULD NOT STAY HERE !!!! ##################
	// ###################################################################################

	private void processInput(Scene scene) {
		/*double angleStep = 0.0025d;
		angle += angleStep;
		if (angle > Math.PI*2d) {
			angle -= Math.PI*2d;
		}*/

		//GraphicalObject[] objects = scene.getObjects();
		Camera camera = scene.getCamera();

		//objects[2].rotateYAroundOrigin(-angleStep); // sun
		//Data.getMoon().rotateYAroundOrigin(angleStep*2d); // moon
		//camera.rotateYaw(-angleStep);
		//scene.getDirectionalLight().rotateY(-angleStep);
		//scene.getPointLights()[1].rotateYAroundOrigin(-angleStep);

		//scene.getShadowMap().setLightAngle(-angle);
		//scene.getShadowMap().cameraChangedPosition();

		double dist = (camera.getRadius() - 0.5d) / 100d;

		if (keyboard.isPressed(GLFW.GLFW_KEY_A)) { // rotate left
			camera.rotateYaw(-dist);
		}
		if (keyboard.isPressed(GLFW.GLFW_KEY_D)) { // rotate right
			camera.rotateYaw(dist);
		}
		if (keyboard.isPressed(GLFW.GLFW_KEY_E)) { // look down
			camera.rotateTilt(-0.005d);
		}
		if (keyboard.isPressed(GLFW.GLFW_KEY_Q)) { // look up
			camera.rotateTilt(0.005d);
		}
		if (keyboard.isPressed(GLFW.GLFW_KEY_W)) { // rotate up
			camera.rotatePitch(-dist);
		}
		if (keyboard.isPressed(GLFW.GLFW_KEY_S)) { // rotate down
			camera.rotatePitch(dist);
		}
		//camera.changeRadius(-dist*mouse.getZSpeed()*3); // scrolling wheel
		if (keyboard.isPressed(GLFW.GLFW_KEY_R)) { // go closer
			camera.changeRadius(-dist);
		}
		if (keyboard.isPressed(GLFW.GLFW_KEY_F)) { // go farther away
			camera.changeRadius(dist);
		}
		if (camera.getRadius() < 1d + GraphicalConstants.ZNEAR) { // ensure not to go too close
			camera.setRadius(1d + GraphicalConstants.ZNEAR);
		}

		if (keyboard.isClicked(GLFW.GLFW_KEY_G)) {
			Data.addScriptRun(new ScriptRun(Data.getMainInstance(), "repopulate", null));
		}
		if (keyboard.isClicked(GLFW.GLFW_KEY_T)) {
			Data.addScriptRun(new ScriptRun(Data.getMainInstance(), "armageddon", null));
		}
		if (keyboard.isClicked(GLFW.GLFW_KEY_O)) {
			Data.addScriptRun(new ScriptRun(Data.getMainInstance(), "fit", null));
		}

		if (keyboard.isClicked(GLFW.GLFW_KEY_X)) {
			GameOptions.printPerformance = !GameOptions.printPerformance;
		}
		if (keyboard.isClicked(GLFW.GLFW_KEY_SPACE)) {
			GameOptions.runTicks = !GameOptions.runTicks;
		}
		if (keyboard.isClicked(GLFW.GLFW_KEY_PERIOD)) {
		    GameOptions.runTicks = true;
		    GameOptions.stopAtNextTick = true;
        }

		if (keyboard.isClicked(GLFW.GLFW_KEY_UP)) {
			nextType(1);
		}
		if (keyboard.isClicked(GLFW.GLFW_KEY_DOWN)) {
			nextType(-1);
		}
		if (mouse.getZSpeed() > 0) {
			for (int i = 0; i < mouse.getZSpeed(); i++) {
				nextType(1);
			}
		}
		if (mouse.getZSpeed() < 0) {
			for (int i = 0; i < -mouse.getZSpeed(); i++) {
				nextType(-1);
			}
		}

		if (mouse.isLeftButtonClicked()) {
			Tile clickedTile = MousePicking.getClickedTile(mouse.getXPos(), mouse.getYPos(), this, scene);
			if (clickedTile != null) {
                //scene.setFacePartOverlay(clickedTile.getTileMesh());
				Data.addScriptRun(new ScriptRun(
						Data.getMainInstance(),
						"leftClick",
						new Variable[]{
								new Variable(clickedTile),
								new Variable(Data.getContainer(GameOptions.currentContainerId))}));
			}
		}
		if (mouse.isRightButtonClicked()) {
			Tile clickedTile = MousePicking.getClickedTile(mouse.getXPos(), mouse.getYPos(), this, scene);
			if (clickedTile != null) {
				if (clickedTile.getSubInstances() == null) {
					GameOptions.selectedInstance = clickedTile;
				} else {
					for (Instance sub : clickedTile.getSubInstances()) {
						GameOptions.selectedInstance = sub;
						break;
					}
				}
			}
			/*if (clickedTile != null) {
				//scene.setFacePartOverlay(clickedTile.getTileMesh());
				Data.addScriptRun(new ScriptRun(
						Data.getMainInstance(),
						"rightClick",
						new Variable[]{
								new Variable(clickedTile),
								new Variable(Data.getContainer(GameOptions.currentContainerId))}));
			}*/
		}

		mouse.flush();
	}

	private void nextType(int direction) {
		int id = GameOptions.currentContainerId + direction;
		while (true) {
			if (id < 0) {
				id += GameConstants.MAX_CONTAINERS;
			} else if (id >= GameConstants.MAX_CONTAINERS) {
				id -= GameConstants.MAX_CONTAINERS;
			}

			Container container = Data.getContainer(id);
			if (container != null) {
				if (container.getType() == DataType.CREATURE
						|| container.getType() == DataType.FORMATION
						|| container.getType() == DataType.TILE
						|| container.getType() == DataType.ENTITY) {
					GameOptions.currentContainerId = id;
					break;
				}
			}
			id += direction;
		}
	}

	// ###################################################################################
	// ################################ Rendering ########################################
	// ###################################################################################

	public void render(Scene scene, GUIInterface hud, Planet planet) {
		this.scene = scene;
		this.hud = hud;
		this.planet = planet;
		this.planetObject = planet != null ? planet.getPlanetObject() : null;

		processInput(scene);

		tasks.forEach(TimedTask::execute);

		flip();

		// closing window
		if (keyboard.isClicked(GLFW.GLFW_KEY_ESCAPE)) {
			//cleanUp(); // somehow enabling this here causes the program to not close properly anymore
			window.close();
		}
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Depth Map

	private void renderDepthMap(ShadowMap shadowMap) {
		Objects.requireNonNull(scene);
		Objects.requireNonNull(hud);
		Objects.requireNonNull(shadowMap);

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
		GL11.glViewport(0, 0, GraphicalConstants.SHADOWMAP_SIZE, GraphicalConstants.SHADOWMAP_SIZE);

		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

		depthShaderProgram.bind();

		Matrix4 viewMatrix = shadowMap.getViewMatrix();
		Matrix4 orthographicProjectionMatrix = shadowMap.getOrthographicProjection();
		depthShaderProgram.setUniform("orthographicProjectionMatrix", orthographicProjectionMatrix);

		for (GraphicalObject object : scene.getObjects()) {
			if (object != null) {
				depthShaderProgram.setUniform("modelLightViewMatrix", viewMatrix.times(object.getWorldMatrix()));

				object.renderForShadowMap();
			}
		}

		if (planetObject != null) {
			depthShaderProgram.setUniform("modelLightViewMatrix", viewMatrix.times(planetObject.getWorldMatrix()));

			// here we pass the shaderProgram because in FacePart.render() we need set some uniforms
			planetObject.render(depthShaderProgram, scene.getCamera().getPlanetaryLODMatrix(), false, false);
		}

		depthShaderProgram.unbind();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

		// shadow map into hud object 0
//		hud.getHUDObjects()[0].getMesh().getMaterial().setTexture(shadowMap.getDepthMap());
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Scene

	private void renderScene() {
		Objects.requireNonNull(scene);

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
			shaderProgram.setUniform("shadowStrength", shadowMap.getShadowStrength());
			shaderProgram.setUniform("shadowEpsilon", shadowMap.getEpsilon());
			shaderProgram.setUniform("lightProjectionMatrix", shadowMap.getOrthographicProjection());
		}

		Matrix4 viewMatrix = camera.getViewMatrix();
		Collection<MeshHub> meshHubs = Data.getMeshHubs();

		// space scenery
		for (GraphicalObject object : scene.getObjects()) {
			if (object != null) {
				renderObject(object, viewMatrix, shadowMap);
			}
		}

		// clear meshHub lists and possibly load meshes
		try {
			for (MeshHub meshHub : meshHubs) {
				meshHub.clear();

				if (!meshHub.isMeshLoaded()) { meshHub.loadMesh(); }
			}
		} catch (Exception e) {
			e.printStackTrace();
			// this happens when a new mesh hub is added to the hashmap while we iterate over it
		}

		// planet
		if (planetObject != null) {
			shaderProgram.setUniform("modelViewMatrix", viewMatrix.times(planetObject.getWorldMatrix()));
			shaderProgram.setUniform("affectedByLight", 1);
			shaderProgram.setUniform("dynamic", 1);

			if (shadowMap != null) {
				shaderProgram.setUniform("modelLightViewMatrix", shadowMap.getViewMatrix().times(planetObject.getWorldMatrix()));
			}

			// here we pass the shaderProgram because in FacePart.render() we need to set some uniforms
			planetObject.render(shaderProgram, camera.getPlanetaryLODMatrix(), true, true);
		}

		// planetary objects
		try {
			for (MeshHub meshHub : meshHubs) {
				meshHub.render(shaderProgram, viewMatrix, shadowMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// this happens when a new mesh hub is added to the hashmap while we iterate over it
		}

		shaderProgram.unbind();
	}

	private void renderObject(GraphicalObject object, Matrix4 viewMatrix, ShadowMap shadowMap) {
		shaderProgram.setUniform("modelViewMatrix", viewMatrix.times(object.getWorldMatrix()));
		shaderProgram.setUniform("affectedByLight", object.isAffectedByLight() ? 1 : 0);
		shaderProgram.setUniform("dynamic", object.isStatic() ? 0 : 1);
		//shaderProgram.setUniform("color", object.getMesh().getTopColor());
		shaderProgram.setUniform("material", object.getMesh().getMaterial());

		if (shadowMap != null) {
			shaderProgram.setUniform("modelLightViewMatrix", shadowMap.getViewMatrix().times(object.getWorldMatrix()));
		}

		object.render();
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% GUI

	private void renderGUI() {
		Objects.requireNonNull(hud);

		hudShaderProgram.bind();

		// set used texture (id = 0)
		hudShaderProgram.setUniform("textureSampler", 0);

		for (GUIObject object : hud.getHUDObjects()) {
			if (object != null) {
				if (aspectRatioHasChanged) {
					object.recalculateScale(window.getWidth(), window.getHeight());
				}

				hudShaderProgram.setUniform("projectionViewMatrix", orthographicMatrix.times(object.getWorldMatrix()));
				//hudShaderProgram.setUniform("color", object.getMesh().getTopColor());
				object.renderForGUI();
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
		if (depthShaderProgram != null) {
			depthShaderProgram.cleanup();
		}
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################


    public Window getWindow() {
        return window;
    }

    public Matrix4 getInvertedPipeline(Scene scene) {
	    return  Data.getPlanet().getPlanetObject().getInvertedWorldMatrix().times(
                scene.getCamera().getInvertedViewMatrix().times(
                invertedProjectionMatrix));
    }

    public double getAspectRatio() {
		return (double) window.getWidth() / (double) window.getHeight();
	}
}