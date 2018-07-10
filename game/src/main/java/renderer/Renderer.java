package renderer;

import engine.window.Window;
import load.FileToString;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import renderer.shaders.ShaderProgram;

/**
 * The renderer is only concerned about periodically drawing the given mesh data onto a window
 */

public class Renderer {

	private Window window;
	private ShaderProgram shaderProgram;

	private int x = 0, y = 0;

	public Renderer(Window window) {

		this.window = window;
	}

	// ###################################################################################
	// ################################ Set Up ###########################################
	// ###################################################################################

	public void initialize() {

		window.initialize();

		// loading and binding the shaders
		try {
			shaderProgram = new ShaderProgram();
			shaderProgram.createVertexShader(FileToString.read("src/main/resources/shaders/vertex.vs"));
			shaderProgram.createFragmentShader(FileToString.read("src/main/resources/shaders/fragment.fs"));
			shaderProgram.link();
		} catch (Exception e) {
			e.printStackTrace();
		}

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

		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3d(1,1,1);
		GL11.glVertex3d(0,y,0);
		GL11.glVertex3d(800,600-y,1);
		GL11.glEnd();

		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glColor3d(1,0,0.5);
		GL11.glVertex3d(x,y,1);
		GL11.glVertex3d(x+100,y,-1);
		GL11.glVertex3d(x+100,y+100,0);

		GL11.glColor3d(0,1,0.5);
		GL11.glVertex3d(800-x,600-y,1);
		GL11.glVertex3d(700-x,600-y,-1);
		GL11.glVertex3d(700-x,500-y,0);
		GL11.glEnd();

		double dt = (double) (System.nanoTime() - t)/1000000;
		System.out.println("rendering took " + dt + " ms");

		x = (x+1)%window.getWidth();
		y = (y+1)%window.getHeight();

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

		shaderProgram.cleanup();
		window.destroy();
	}
}