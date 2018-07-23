package engine.graphics.renderer;

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
import engine.math.numericalObjects.Vector4;
import load.OBJLoader;
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

	private double zNear = 0.001d;
	private double zFar = 1000d;

	private Matrix4 projectionMatrix;

	private GraphicalObject[] objects;
	private Camera camera;

	private AmbientLight ambientLight;
	private DirectionalLight directionalLight;
	private PointLight pointLight;
	private SpotLight spotLight;

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
		Texture cubeTexture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/space_cube.png");
		Texture grasTexture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/gras.png");
		Texture icoTexture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/ico_wireframe.png");

		objects = new GraphicalObject[4];
		double sunDistance = 10;

		//objects[0] = new GraphicalObject(OBJLoader.loadMesh("data/mods/vanilla/assets/meshes/monkey.obj"));
		objects[0] = new GraphicalObject(MeshGenerator.createIcosahedron());
		objects[0].setTexture(icoTexture);
		objects[0].scale(3,3,3);
		objects[0].rotate(0,0,Math.PI/8);
		objects[0].getMesh().getMaterial().setSpecularPower(4);
		objects[0].getMesh().getMaterial().setReflectanceStrength(new RGBA(1,0.5,0.5,0));

		objects[1] = new GraphicalObject(MeshGenerator.createIcosahedron());
		objects[1].scale(1,1,1);
		objects[1].setPosition(0,0,-sunDistance);
		objects[1].setColor(1,1,0.5f);
		objects[1].setAffectedByLight(false);

		// background
		objects[2] = new GraphicalObject(MeshGenerator.createCube(true));
		objects[2].setTexture(cubeTexture);
		objects[2].setAffectedByLight(false);
		objects[2].scale(sunDistance*2,sunDistance*2,sunDistance*2);

		objects[3] = new GraphicalObject(MeshGenerator.createIcosahedron());
		objects[3].setTexture(icoTexture);
		objects[3].scale(0.5,0.5,0.5);
		objects[3].setPosition(5,0,0);

		pointLight = new PointLight(1,0,0);
		pointLight.setAttenuation(Attenuation.CONSTANT());
		pointLight.setPosition(0,0,-sunDistance);

		spotLight = new SpotLight(0,1,0);
		spotLight.setAttenuation(Attenuation.CONSTANT());
		spotLight.setPosition(0,0,-sunDistance);
		spotLight.setDirection(new Vector3(0,0,1));
		spotLight.setConeAngle(Math.PI/32);

		directionalLight = new DirectionalLight(0,0,1);
		directionalLight.setDirection(new Vector3(0,0,1));

		ambientLight = new AmbientLight(0.5,0.5,0.5);
	}

	private void initializeUniforms() {
		try {
			shaderProgram.createUniform("modelViewMatrix");
			shaderProgram.createUniform("projectionMatrix");
			shaderProgram.createUniform("textureSampler");
			shaderProgram.createUniform("color");
			shaderProgram.createUniform("affectedByLight");
			shaderProgram.createUniform("ambientLight");

			shaderProgram.createDirectionalLightUniform("directionalLight");
			shaderProgram.createPointLightUniform("pointLight");
			shaderProgram.createSpotLightUniform("spotLight");
			shaderProgram.createMaterialUniform("material");
		} catch (Exception e) {
			e.printStackTrace();
			exit(1);
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
		objects[3].rotateYAroundOrigin(angleStep*2);
		directionalLight.rotateY(-angleStep);
		pointLight.rotateYAroundOrigin(-angleStep);
		spotLight.rotateYAroundOrigin(-angleStep);
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
		// set directional light uniforms
		directionalLight.actualize(camera.getViewMatrix());
		shaderProgram.setUniform("directionalLight",directionalLight);
		// set point light uniforms
		pointLight.actualize(camera.getViewMatrix());
		shaderProgram.setUniform("pointLight",pointLight);
		// set spot light uniforms
		spotLight.actualize(camera.getViewMatrix());
		shaderProgram.setUniform("spotLight",spotLight);
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