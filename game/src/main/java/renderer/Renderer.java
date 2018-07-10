package renderer;

import engine.window.Window;
import static org.lwjgl.opengl.GL11.*;

/**
 * The renderer is only concerned about periodically drawing the given world data onto a window
 */

public class Renderer {

	private Window window;
	private int x = 0, y = 0;

	public Renderer(Window window) {

		this.window = window;
	}

	public void initialize() {

		window.initialize();

		// setting up the projection matrix
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0,window.getWidth(),0,window.getHeight(),1,-1);
		glMatrixMode(GL_MODELVIEW);
	}

	public void render() {

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glBegin(GL_LINES);

		glColor3d(1,1,1);
		glVertex3f(0,0,0);
		glVertex3f(800,600,100);

		glEnd();
	}

	public boolean displayExists() {
		return !window.isClosed();
	}

	public void destroy() {
		window.destroy();
	}

	public void setFps(int fps) {
		window.setFps(fps);
	}

	public void sync() {
		window.update();
		window.sync();

		/*try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
	}

}
