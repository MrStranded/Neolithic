package renderer;

import engine.window.Window;
import load.FileToString;
import math.Vector3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
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

	public void calculateProjectionMatrix() {

		// setting up the projection matrix
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0,window.getWidth(),0,window.getHeight(),1,-1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	// ###################################################################################
	// ################################ Rendering ########################################
	// ###################################################################################

	public void render() {

		long t = System.nanoTime();

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		shaderProgram.bind();

		// Bind to the VAO
		GL30.glBindVertexArray(mesh.getVertexArrayObjectId());
		GL20.glEnableVertexAttribArray(0);

		// Draw the vertices
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.getVertexCount());

		// Restore state
		GL20.glDisableVertexAttribArray(0);
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