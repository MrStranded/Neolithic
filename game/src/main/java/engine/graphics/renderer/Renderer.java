package engine.graphics.renderer;

import engine.graphics.objects.light.AmbientLight;
import engine.graphics.objects.light.Attenuation;
import engine.graphics.objects.light.PointLight;
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
import engine.math.numericalObjects.Vector4;
import load.OBJLoader;
import load.StringLoader;
import load.TextureLoader;
import engine.math.numericalObjects.Matrix4;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import engine.graphics.objects.generators.MeshGenerator;

/**
 * The renderer is only concerned about periodically drawing the given mesh data onto a window
 */

public class Renderer {

	private Window window;
	private ShaderProgram shaderProgram;

	private double zNear = 0.001d;
	private double zFar = 15000d;

	private Matrix4 projectionMatrix;

	private GraphicalObject[] objects;
	private PointLight pointLight;
	private Camera camera;
	private AmbientLight ambientLight;

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

	private void initializeVertexObjects() {

		Texture trollFace = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/trollface.png");
		Texture cubeTexture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/space.png");
		Texture grasTexture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/gras.png");
		Texture icoTexture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/ico_wireframe.png");

		objects = new GraphicalObject[3];

		//objects[0] = new GraphicalObject(MeshGenerator.createIcosahedron());
		try {
			//objects[0] = new GraphicalObject(OBJLoader.loadMesh("data/mods/vanilla/assets/meshes/monkey.obj"));
			//objects[0].getMesh().randomizeTextureCoordinates();
			objects[0] = new GraphicalObject(MeshGenerator.createIcosahedron());
			//objects[0] = new GraphicalObject(MeshGenerator.createCube(true));
		} catch (Exception e) {
			e.printStackTrace();
		}
		objects[0].setTexture(icoTexture);
		objects[0].scale(3,3,3);
		objects[0].rotate(0,0,Math.PI/8);
		objects[0].getMesh().getMaterial().setSpecularPower(4);
		objects[0].getMesh().getMaterial().setReflectanceStrength(new RGBA(0,0,1,0));

		objects[1] = new GraphicalObject(MeshGenerator.createIcosahedron());
		objects[1].scale(100,100,100);
		objects[1].setPosition(0,0,-5000);
		objects[1].setColor(1,1,0);
		objects[1].setAffectedByLight(false);

		objects[2] = new GraphicalObject(MeshGenerator.createCube(true));
		objects[2].setTexture(cubeTexture);
		objects[2].setAffectedByLight(false);
		objects[2].scale(6000,6000,6000);

		pointLight = new PointLight(1,1,1);
		pointLight.setAttenuation(Attenuation.CONSTANT());
		pointLight.setPosition(0,0,-5000);

		ambientLight = new AmbientLight(0.5,0.25,0.25);
	}

	private void initializeUniforms() {
		try {
			shaderProgram.createUniform("modelViewMatrix");
			shaderProgram.createUniform("projectionMatrix");
			shaderProgram.createUniform("textureSampler");
			shaderProgram.createUniform("color");
			shaderProgram.createUniform("affectedByLight");
			shaderProgram.createUniform("ambientLight");

			shaderProgram.createPointLightUniform("pointLight");
			shaderProgram.createMaterialUniform("material");
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
		pointLight.rotateYAroundOrigin(-angleStep);

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
			objects[0].setTexture(TextureLoader.loadTexture("data/mods/vanilla/assets/textures/trollface.png"));
		}
		if (keyboard.isClicked(GLFW.GLFW_KEY_G)) { // gras tex
			objects[0].setTexture(TextureLoader.loadTexture("data/mods/vanilla/assets/textures/gras.png"));
		}

		//Vector3 cameraPosition = camera.getViewMatrix().times(new Vector4(0,0,0,1)).extractVector3();
		//objects[2].setPosition(cameraPosition);

		long t = System.nanoTime();

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		shaderProgram.bind();

		// upload projection matrix
		shaderProgram.setUniform("projectionMatrix", projectionMatrix);
		// set used texture (id = 0)
		shaderProgram.setUniform("textureSampler", 0);
		// set point light uniforms
		pointLight.actualizeViewPosition(camera.getViewMatrix());
		shaderProgram.setUniform("pointLight",pointLight);
		// set ambient light
		shaderProgram.setUniform("ambientLight",ambientLight.getColor());

		for (GraphicalObject object : objects) {
			Mesh mesh = object.getMesh();
			shaderProgram.setUniform("modelViewMatrix", camera.getViewMatrix().times(object.getWorldMatrix()));
			shaderProgram.setUniform("color", mesh.getColor());
			shaderProgram.setUniform("affectedByLight", object.isAffectedByLight() ? 1 : 0);
			shaderProgram.setUniform("material",object.getMesh().getMaterial());
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