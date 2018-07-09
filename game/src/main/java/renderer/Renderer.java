package renderer;

import engine.window.Screen;
import engine.window.Window;

import java.awt.*;


/**
 * The renderer is only concerned about periodically drawing the given world data onto a screen - it's backbuffer.
 */

public class Renderer {

	private Window window;
	private Screen screen;
	private int x = 0, y = 0;

	public Renderer(Window window) {
		this.window = window;
	}

	public void render() {

		/*clear();

		Graphics g = screen.getGraphics();

		g.drawRect(x,y,10,10);
		screen.drawTestImage(g);

		x = (x+1)%(screen.getWidth()-10);
		y = (y+1)%(screen.getHeight()-10);

		flip();*/
	}

	private void clear() {
		screen.clear();
	}

	private void flip() {
		screen.getBufferStrategy().show();
	}

	public boolean displayExists() {
		return false;
	}

}
