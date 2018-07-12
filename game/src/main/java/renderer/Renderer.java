package renderer;

import engine.window.Window;
import load.FileToString;
import math.Matrix4;
import math.Vector3;
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

	private double fieldOfView = 1.0d;
	private double zNear = 0.01d;
	private double zFar = 1000d;

	private Matrix4 projectionMatrix;

	private Mesh mesh;

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

		//mesh = MeshGenerator.createTetrahedron();
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

		projectionMatrix = Projection.createProjectionMatrix(1d, aspectRatio, zNear, zFar);

		// setting up the projection matrix
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(-aspectRatio, aspectRatio, -1, 1,1,-1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	// ###################################################################################
	// ################################ Rendering ########################################
	// ###################################################################################

	public void render() {

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

	public void setFps(int fps) {
		window.setFps(fps);
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