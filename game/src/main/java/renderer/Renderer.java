package renderer;

import engine.objects.GraphicalObject;
import engine.window.Window;
import load.FileToString;
import math.Matrix4;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderer.projection.Projection;
import renderer.shaders.ShaderProgram;
import renderer.shapes.Mesh;
import renderer.shapes.utils.MeshGenerator;

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

		objects = new GraphicalObject[3];

		objects[0] = new GraphicalObject(MeshGenerator.createQuad(1d));
		objects[1] = new GraphicalObject(MeshGenerator.createQuad(2d));
		objects[2] = new GraphicalObject(MeshGenerator.createQuad(0.5d));

		objects[0].translate(-0.5d,0.25d,-1d);
		objects[1].translate(1d,-0.5d,-2d);
		objects[2].translate(0,-0.25d,-1d);

		objects[2].scale(2d,0.5d,1d);
	}

	private void initializeUniforms() {

		try {
			shaderProgram.createUniform("projectionMatrix");
			shaderProgram.createUniform("worldMatrix");
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

		/*double r = 1d;
		angle -= 0.01;
		if (angle > Math.PI*2d) {
			angle -= Math.PI*2d;
		}
		float z = -1f + (float) (Math.sin(angle)*r);
		mesh.setZValues(z);
		mesh.registerData();
		if (z < -1f-r*0.9f) {
			Vector4 v = new Vector4(1,1,z,1);
			System.out.println("---------------");
			System.out.println(v);
			System.out.println(projectionMatrix.times(v));
		}*/

		long t = System.nanoTime();

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		shaderProgram.bind();

		// upload projection matrix
		shaderProgram.setUniform("projectionMatrix", projectionMatrix);

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