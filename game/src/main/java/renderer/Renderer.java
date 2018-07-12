package renderer;

import engine.window.Window;
import load.FileToString;
import math.Matrix4;
import math.Vector3;
import math.Vector4;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import renderer.projection.Projection;
import renderer.shaders.ShaderProgram;
import renderer.shapes.Mesh;
import renderer.shapes.Triangle;
import utils.MeshGenerator;

import java.nio.FloatBuffer;

/**
 * The renderer is only concerned about periodically drawing the given mesh data onto a window
 */

public class Renderer {

	private Window window;
	private ShaderProgram shaderProgram;

	private double zNear = 1d; // freaking out for values <1
	private double zFar = 1000d;

	private Matrix4 projectionMatrix;

	private Mesh mesh;
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

		mesh = MeshGenerator.createQuad();
	}

	private void initializeUniforms() {

		try {
			shaderProgram.createUniform("projectionMatrix");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void calculateProjectionMatrix() {

		double aspectRatio = (double) window.getWidth()/(double) window.getHeight();

		projectionMatrix = Projection.createProjectionMatrix(-aspectRatio,aspectRatio,1d,-1d,zNear,zFar);
	}

	// ###################################################################################
	// ################################ Rendering ########################################
	// ###################################################################################

	public void render() {

		double r = 1d;
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
		}

		long t = System.nanoTime();

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		shaderProgram.bind();

		// upload projection matrix
		shaderProgram.setUniform("projectionMatrix", projectionMatrix);

		// Bind to the VAO
		GL30.glBindVertexArray(mesh.getVertexArrayObjectId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		// Draw the mesh
		GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

		// Restore state
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);

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

		mesh.cleanUp();

		window.destroy();
	}
}