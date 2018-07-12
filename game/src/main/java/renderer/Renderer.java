package renderer;

import engine.objects.GraphicalObject;
import engine.window.Window;
import load.FileToString;
import load.TextureLoader;
import math.Matrix4;
import org.lwjgl.opengl.GL11;
import renderer.data.Texture;
import renderer.projection.Projection;
import renderer.shaders.ShaderProgram;
import renderer.data.utils.MeshGenerator;

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

		objects = new GraphicalObject[5];

		objects[0] = new GraphicalObject(MeshGenerator.createQuad(1d, trollFace));
		objects[1] = new GraphicalObject(MeshGenerator.createQuad(2d, trollFace));
		objects[2] = new GraphicalObject(MeshGenerator.createQuad(0.5d, cubeTexture));
		objects[3] = new GraphicalObject(MeshGenerator.createQuad(1d, cubeTexture));

		objects[4] = new GraphicalObject(MeshGenerator.createIcosahedron(trollFace));

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
		objects[4].translate(0,0,-1.25d);
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
		objects[0].rotateX(angleStep);
		objects[1].rotateY(angleStep);
		objects[2].rotateZ(angleStep);
		objects[3].rotate(angleStep,angleStep,angleStep);
		objects[4].setRotation(0,angle,0);

		long t = System.nanoTime();

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		shaderProgram.bind();

		// upload projection matrix
		shaderProgram.setUniform("projectionMatrix", projectionMatrix);
		// set used texture (id = 0)
		shaderProgram.setUniform("textureSampler", 0);

		//System.out.println("-----------");
		for (GraphicalObject object : objects) {
			//System.out.println(object.getWorldMatrix());
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