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

		long t = System.nanoTime();

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glBegin(GL_LINES);
		glColor3d(1,1,1);
		glVertex3d(0,y,0);
		glVertex3d(800,600-y,1);
		glEnd();

		glBegin(GL_TRIANGLES);
		glColor3d(1,0,0.5);
		glVertex3d(x,y,1);
		glVertex3d(x+100,y,-1);
		glVertex3d(x+100,y+100,0);

		glColor3d(0,1,0.5);
		glVertex3d(800-x,600-y,1);
		glVertex3d(700-x,600-y,-1);
		glVertex3d(700-x,500-y,0);
		glEnd();

		double dt = (double) (System.nanoTime() - t)/1000000;
		System.out.println("rendering took " + dt + " ms");

		x = (x+1)%window.getWidth();
		y = (y+1)%window.getHeight();
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
	}

}
