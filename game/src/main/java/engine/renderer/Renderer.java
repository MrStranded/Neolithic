package engine.renderer;

import engine.input.KeyboardInput;
import engine.input.MouseInput;
import engine.objects.GraphicalObject;
import engine.objects.Camera;
import engine.data.Mesh;
import engine.data.Texture;
import engine.renderer.projection.Projection;
import engine.renderer.shaders.ShaderProgram;
import engine.window.Window;
import load.FileToString;
import load.TextureLoader;
import math.Matrix4;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import engine.data.utils.MeshGenerator;

/**
 * The renderer is only concerned about periodically drawing the given mesh data onto a window
 */

public class Renderer {

	private Window window;
	private ShaderProgram shaderProgram;

	private double zNear = 0.001d;
	private double zFar = 10000d;

	private Matrix4 projectionMatrix;

	private GraphicalObject[] objects;
	private Camera camera;

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
		initializeVertexObjects();
		initializeUniforms();
		initializeCamera();
		initializeInput();

		calculateProjectionMatrix();

		//GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	private void initializeShaders() {

		// loading and binding the shaders
		try {
			shaderProgram = new ShaderProgram();
			shaderProgram.createVertexShader(FileToString.read("src/main/resources/shaders/vertex.vs"));
			shaderProgram.createFragmentShader(FileToString.read("src/main/resources/shaders/fragment.fs"));
			shaderProgram.link();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initializeVertexObjects() {

		Texture trollFace = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/trollface.png");
		Texture cubeTexture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/cube_texture.png");
		Texture grasTexture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/gras.png");

		objects = new GraphicalObject[2];

		objects[0] = new GraphicalObject(MeshGenerator.createIcosahedron());
		objects[0].setTexture(grasTexture);
		objects[0].scale(3,3,3);
		objects[0].rotate(0,0,Math.PI/8);

		objects[1] = new GraphicalObject(MeshGenerator.createIcosahedron());
		objects[1].scale(100,100,100);
		objects[1].setPosition(0,0,-5000);
		objects[1].setColor(1,1,0);
	}

	private void initializeUniforms() {

		try {
			shaderProgram.createUniform("viewMatrix");
			shaderProgram.createUniform("worldMatrix");
			shaderProgram.createUniform("textureSampler");
			shaderProgram.createUniform("color");
			shaderProgram.createUniform("colorOnly");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initializeCamera() {

		camera = new Camera();
		camera.setRadius(5);
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

	// ###################################################################################
	// ################################ Rendering ########################################
	// ###################################################################################

	public void render() {

		double angleStep = 0.0025d;
		angle += angleStep;
		if (angle > Math.PI*2d) {
			angle -= Math.PI*2d;
		}

		objects[0].rotateY(angleStep);
		objects[1].rotateYAroundOrigin(-angleStep);

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
			camera.changeRadius(-0.01d);
		}
		if (keyboard.isPressed(GLFW.GLFW_KEY_F)) { // go farther
			camera.changeRadius(0.01d);
		}

		if (keyboard.isClicked(GLFW.GLFW_KEY_T)) { // troll tex
			objects[0].setTexture(TextureLoader.loadTexture("data/mods/vanilla/assets/textures/trollface.png"));
		}
		if (keyboard.isClicked(GLFW.GLFW_KEY_G)) { // gras tex
			objects[0].setTexture(TextureLoader.loadTexture("data/mods/vanilla/assets/textures/gras.png"));
		}

		long t = System.nanoTime();

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		shaderProgram.bind();

		// upload projection matrix
		shaderProgram.setUniform("viewMatrix", projectionMatrix.times(camera.getViewMatrix()));
		// set used texture (id = 0)
		shaderProgram.setUniform("textureSampler", 0);

		for (GraphicalObject object : objects) {
			Mesh mesh = object.getMesh();
			shaderProgram.setUniform("worldMatrix", object.getWorldMatrix());
			shaderProgram.setUniform("color", mesh.getColor());
			shaderProgram.setUniform("colorOnly", mesh.hasTexture()? 0 : 1);
			object.render();
		}

		shaderProgram.unbind();

		double dt = (double) (System.nanoTime() - t)/1000000;
		//System.out.println("rendering took " + dt + " ms");

		flip();

		// closing window
		if (keyboard.isClicked(GLFW.GLFW_KEY_ESCAPE)) {
			window.close();
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

		for (GraphicalObject object : objects) {
			object.cleanUp();
		}

		window.destroy();
	}
}