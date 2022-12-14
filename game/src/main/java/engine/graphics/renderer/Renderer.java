package engine.graphics.renderer;

import constants.GraphicalConstants;
import constants.ResourcePathConstants;
import engine.TimedTask;
import engine.data.Data;
import engine.data.planetary.Planet;
import engine.graphics.gui.GUIInterface;
import engine.graphics.gui.GuiData;
import engine.graphics.gui.window.Window;
import engine.graphics.objects.Camera;
import engine.graphics.objects.GraphicalObject;
import engine.graphics.objects.MeshHub;
import engine.graphics.objects.Scene;
import engine.graphics.objects.light.PointLight;
import engine.graphics.objects.light.ShadowMap;
import engine.graphics.objects.light.SpotLight;
import engine.graphics.objects.planet.PlanetObject;
import engine.graphics.renderer.projection.Projection;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.input.IngameInteractions;
import engine.input.MouseInput;
import engine.math.MatrixCalculations;
import engine.math.numericalObjects.Matrix4;
import engine.parser.utils.Logger;
import load.StringLoader;
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

	private IngameInteractions ingameInteractions;

	private List<TimedTask> tasks = Collections.emptyList();

	private Scene scene = null;
	private GUIInterface hud = null;
	private PlanetObject planetObject = null;

	public Renderer(Window window) {
		this.window = window;
		window.setRenderer(this);

		initialize();
	}

	// ###################################################################################
	// ################################ Set Up ###########################################
	// ###################################################################################

	public void initialize() {
		initializeInput();

		initializeShaders();
		initializeHUDShaders();
		initializeDepthShaders();

		initializeUniforms();

		calculateProjectionMatrizes();
		calculateOrthographicMatrix();

		initializeTasks();
	}

	private void initializeInput() {
		ingameInteractions = new IngameInteractions(window, this);
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
			shaderProgram.createUniform("affectedByShadow");
			shaderProgram.createUniform("flipLightDirection");
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
			hudShaderProgram.createUniform("horizontalStep");
			hudShaderProgram.createUniform("verticalStep");
			//hudShaderProgram.createUniform("color");

			// ---------------------------------------------------------- shadow maps
			depthShaderProgram.createUniform("orthographicProjectionMatrix");
			depthShaderProgram.createUniform("modelLightViewMatrix");

		} catch (Exception e) {
			e.printStackTrace();
			exit(1);
		}
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

		if (hud != null) {
			hud.resize();
		}
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
	// ################################ Rendering ########################################
	// ###################################################################################

	public void render(Scene scene, GUIInterface hud, Planet planet) {
		this.scene = scene;
		this.hud = hud;
		this.planetObject = planet != null ? planet.getPlanetObject() : null;

		ingameInteractions.processInput(scene);

		tasks.forEach(TimedTask::execute);

		flip();
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
			Matrix4 modelLightViewMatrix = viewMatrix.times(planetObject.getWorldMatrix());
			depthShaderProgram.setUniform("modelLightViewMatrix", modelLightViewMatrix);

			// here we pass the shaderProgram because in FacePart.render() we need to set some uniforms
			planetObject.render(depthShaderProgram, scene.getCamera().getPlanetaryLODMatrix(), false, false);
		}

		// planetary objects
		try {
			for (MeshHub meshHub : Data.getMeshHubs()) {
				if (meshHub.isOpaque()) {
					meshHub.renderForShadowMap(depthShaderProgram, viewMatrix, shadowMap);
				}
			}
		} catch (ConcurrentModificationException e) {
			// this happens when a new mesh hub is added to the hashmap while we iterate over it
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
			if (object != null && object.isRenderInScene()) {
				renderObject(object, viewMatrix, shadowMap);
			}
		}

		// clear meshHub lists and possibly load meshes
		try {
			for (MeshHub meshHub : meshHubs) {
				meshHub.clear();

				if (! meshHub.isMeshLoaded()) {
					meshHub.loadMesh();
				}
			}
		} catch (ConcurrentModificationException e) {
			Logger.error("ConcurrentModificationException during rendering");
		} catch (Exception e) {
			e.printStackTrace();
			// this happens when a new mesh hub is added to the hashmap while we iterate over it
		}

		// planet
		if (planetObject != null) {
			shaderProgram.setUniform("modelViewMatrix", viewMatrix.times(planetObject.getWorldMatrix()));
			shaderProgram.setUniform("affectedByLight", 1);
			shaderProgram.setUniform("affectedByShadow", 1);
			shaderProgram.setUniform("flipLightDirection", 1);
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
				if (meshHub.isOpaque()) {
					meshHub.render(shaderProgram, viewMatrix, shadowMap);
				}
			}
			for (MeshHub meshHub : meshHubs) {
				if (! meshHub.isOpaque()) {
					meshHub.render(shaderProgram, viewMatrix, shadowMap);
				}
			}
		} catch (ConcurrentModificationException e) {
			// this happens when a new mesh hub is added to the hashmap while we iterate over it
		}

		shaderProgram.unbind();
	}

	private void renderObject(GraphicalObject object, Matrix4 viewMatrix, ShadowMap shadowMap) {
		shaderProgram.setUniform("modelViewMatrix", viewMatrix.times(object.getWorldMatrix()));
		shaderProgram.setUniform("affectedByLight", object.isAffectedByLight() ? 1 : 0);
		shaderProgram.setUniform("affectedByShadow", object.isAffectedByShadow() ? 1 : 0);
		shaderProgram.setUniform("flipLightDirection", object.isLightDirectionFlipped() ? -1 : 1);
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
		hudShaderProgram.setUniform("horizontalStep", 1f / GuiData.getFontTexture().getWidth());
		hudShaderProgram.setUniform("verticalStep", 1f / GuiData.getFontTexture().getHeight());

		hud.render(hudShaderProgram, orthographicMatrix);

		hudShaderProgram.unbind();
	}

	public void updateGui(MouseInput mouse) {
		if (hud != null) {
			hud.updateGui(mouse);
		}
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