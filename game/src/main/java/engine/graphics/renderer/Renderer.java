package engine.graphics.renderer;

import engine.graphics.GraphicalConstants;
import engine.graphics.gui.HUDInterface;
import engine.graphics.objects.Scene;
import engine.graphics.objects.light.*;
import engine.graphics.renderer.color.RGBA;
import engine.graphics.renderer.projection.Projection;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.input.KeyboardInput;
import engine.input.MouseInput;
import engine.graphics.objects.GraphicalObject;
import engine.graphics.objects.Camera;
import engine.graphics.objects.models.Mesh;
import engine.graphics.objects.models.Texture;
import engine.graphics.window.Window;
import engine.math.numericalObjects.Vector3;
import load.StringLoader;
import load.TextureLoader;
import engine.math.numericalObjects.Matrix4;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import engine.graphics.objects.generators.MeshGenerator;

import static java.lang.System.exit;

/**
 * The renderer is only concerned about periodically drawing the given mesh data onto a window
 */

public class Renderer {

	private Window window;
	private ShaderProgram shaderProgram;
	private ShaderProgram hudShaderProgram;

	private double zNear = 0.001d;
	private double zFar = 1000d;

	private Matrix4 projectionMatrix;
	private Matrix4 orthographicMatrix;

	private HUDInterface hudInterface;

	private MouseInput mouse;
	private KeyboardInput keyboard;

	private double angle = 0;

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
		// loading and binding the shaders
		try {
			shaderProgram = new ShaderProgram();
			shaderProgram.createVertexShader(StringLoader.read("src/main/resources/shaders/vertex.vs"));
			shaderProgram.createFragmentShader(StringLoader.read("src/main/resources/shaders/fragment.fs"));
			shaderProgram.link();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initializeHUDShaders() {
		// loading and binding the shaders
		try {
			hudShaderProgram = new ShaderProgram();
			hudShaderProgram.createVertexShader(StringLoader.read("src/main/resources/shaders/orthoVertex.vs"));
			hudShaderProgram.createFragmentShader(StringLoader.read("src/main/resources/shaders/orthoFragment.fs"));
			hudShaderProgram.link();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void initializeUniforms() {
		try {
			shaderProgram.createUniform("modelViewMatrix");
			shaderProgram.createUniform("projectionMatrix");
			shaderProgram.createUniform("textureSampler");
			shaderProgram.createUniform("color");
			shaderProgram.createUniform("affectedByLight");
			shaderProgram.createUniform("dynamic");
			shaderProgram.createUniform("ambientLight");

			shaderProgram.createDirectionalLightUniform("directionalLight");
			shaderProgram.createMaterialUniform("material");

			for (int i = 0; i<GraphicalConstants.MAX_POINT_LIGHTS; i++) {
				shaderProgram.createPointLightUniform("pointLight[" + i + "]");
			}
			for (int i = 0; i<GraphicalConstants.MAX_SPOT_LIGHTS; i++) {
				shaderProgram.createSpotLightUniform("spotLight[" + i + "]");
			}
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

	public void calculateProjectionMatrix() {
		double aspectRatio = (double) window.getWidth()/(double) window.getHeight();

		projectionMatrix = Projection.createPerspectiveProjectionMatrix(-aspectRatio*zNear,aspectRatio*zNear,1d*zNear,-1d*zNear,zNear,zFar);
	}

	public void calculateOrthographicMatrix() {
		double aspectRatio = (double) window.getWidth()/(double) window.getHeight();

		orthographicMatrix = Projection.createOrthographicProjectionMatrix(-aspectRatio*zNear,aspectRatio*zNear,1d*zNear,-1d*zNear,zNear,zFar);
	}

	// ###################################################################################
	// ################################ Rendering ########################################
	// ###################################################################################

	public void render(Scene scene, HUDInterface hudInterface) {
		double angleStep = 0.0025d;
		angle += angleStep;
		if (angle > Math.PI*2d) {
			angle -= Math.PI*2d;
		}

		GraphicalObject[] objects = scene.getObjects();
		Camera camera = scene.getCamera();

		objects[1].rotateY(angleStep);
		objects[2].rotateYAroundOrigin(-angleStep);
		objects[3].rotateYAroundOrigin(angleStep*2);
		scene.getDirectionalLight().rotateY(-angleStep);
		scene.getPointLights()[1].rotateYAroundOrigin(-angleStep);
		scene.getSpotLights()[5].rotateYAroundOrigin(-angleStep);
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

		if (keyboard.isClicked(GLFW.GLFW_KEY_T)) { // troll tex
			objects[1].setTexture(TextureLoader.loadTexture("data/mods/vanilla/assets/textures/trollface.png"));
		}
		if (keyboard.isClicked(GLFW.GLFW_KEY_G)) { // gras tex
			objects[1].setTexture(TextureLoader.loadTexture("data/mods/vanilla/assets/textures/gras.png"));
		}

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		renderScene(scene);
		renderHUD(hudInterface);

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

		for (GraphicalObject object : scene.getObjects()) {
			Mesh mesh = object.getMesh();
			shaderProgram.setUniform("modelViewMatrix", camera.getViewMatrix().times(object.getWorldMatrix()));
			shaderProgram.setUniform("color", mesh.getColor());
			shaderProgram.setUniform("affectedByLight", object.isAffectedByLight() ? 1 : 0);
			shaderProgram.setUniform("dynamic", object.isStatic() ? 0 : 1);
			shaderProgram.setUniform("material", object.getMesh().getMaterial());
			object.render();
		}

		shaderProgram.unbind();
	}

	private void renderHUD(HUDInterface hudInterface) {

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

		window.destroy();
	}
}