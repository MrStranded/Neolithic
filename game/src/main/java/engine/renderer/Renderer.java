package engine.renderer;

import engine.objects.GraphicalObject;
import engine.objects.Camera;
import engine.renderer.data.Texture;
import engine.renderer.projection.Projection;
import engine.renderer.shaders.ShaderProgram;
import engine.window.Window;
import load.FileToString;
import load.TextureLoader;
import math.Matrix4;
import org.lwjgl.opengl.GL11;
import engine.renderer.data.utils.MeshGenerator;

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

		objects = new GraphicalObject[5];

		objects[0] = new GraphicalObject(MeshGenerator.createQuad(1d, trollFace));
		objects[1] = new GraphicalObject(MeshGenerator.createQuad(2d, trollFace));
		objects[2] = new GraphicalObject(MeshGenerator.createQuad(0.5d, cubeTexture));
		objects[3] = new GraphicalObject(MeshGenerator.createQuad(1d, cubeTexture));

		objects[4] = new GraphicalObject(MeshGenerator.createIcosahedron(grasTexture));

		objects[2].scale(2d,0.5d,1d);
		objects[3].setScale(1d,1d,0d);

		/*objects[0].rotate(Math.toRadians(30),0,0);
		objects[1].setRotation(0,Math.toRadians(30),0);
		objects[2].rotate(0,0,Math.toRadians(30));
		objects[3].setRotation(Math.toRadians(30),Math.toRadians(30),Math.toRadians(30));
		*/
		objects[0].translate(-0.5d,0.25d,-1d);
		objects[1].translate(1d,-0.5d,-1d);
		objects[2].setPosition(0,-0.25d,-1d);
		objects[3].setPosition(0,0,-1d);

		objects[4].scale(0.5,0.5,0.5);
		objects[4].translate(0,0,0.5d);

		objects[0].setColor(1,1,1);
		objects[2].setColor(1,1,0);
		objects[4].setColor(1,1,1);

		objects[4].setUseDepthTest(false);
	}

	private void initializeUniforms() {

		try {
			shaderProgram.createUniform("projectionMatrix");
			shaderProgram.createUniform("worldMatrix");
			shaderProgram.createUniform("textureSampler");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initializeCamera() {

		camera = new Camera();
		camera.translate(0,0,2);
		//camera.rotateAroundOrigin(0,Math.toRadians(90),0);
		//camera.setRotation(0,Math.toRadians(90),0);
		//camera.translate(4,0,0);
	}

	public void calculateProjectionMatrix() {

		double aspectRatio = (double) window.getWidth()/(double) window.getHeight();

		projectionMatrix = Projection.createProjectionMatrix(-aspectRatio*zNear,aspectRatio*zNear,1d*zNear,-1d*zNear,zNear,zFar);
	}

	// ###################################################################################
	// ################################ Rendering ########################################
	// ###################################################################################

	public void render() {

		double angleStep = 0.01d;
		angle += angleStep;
		if (angle > Math.PI*2d) {
			angle -= Math.PI*2d;
		}
		objects[0].rotate(angleStep,0,0);
		objects[1].rotateYAroundOrigin(angleStep);
		objects[2].rotateAroundOrigin(0,0,angleStep);
		objects[3].rotate(angleStep,angleStep,angleStep);
		objects[4].setRotation(0,angle,0);

		objects[2].setScale(Math.abs(Math.cos(angle)*5d),Math.abs(Math.sin(angle)*2d),0d);

		objects[0].move(Math.cos(angle)/100d,0,0.01d);
		objects[4].translate(0,0,Math.cos(angle)/50d);

		//camera.rotateAroundOrigin(0,angleStep,0);
		camera.move(0,0,Math.cos(angle)/50d);

		long t = System.nanoTime();

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		shaderProgram.bind();

		// upload projection matrix
		shaderProgram.setUniform("projectionMatrix", projectionMatrix.times(camera.getViewMatrix()));
		// set used texture (id = 0)
		shaderProgram.setUniform("textureSampler", 0);

		for (GraphicalObject object : objects) {
			shaderProgram.setUniform("worldMatrix", object.getWorldMatrix());
			object.render();
		}

		shaderProgram.unbind();

		double dt = (double) (System.nanoTime() - t)/1000000;
		//System.out.println("rendering took " + dt + " ms");

		flip();
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