package renderer;

import engine.window.Screen;
import renderer.color.RGBA;
import renderer.image.Image;

import javax.swing.*;
import java.awt.*;

/**
 * The renderer is only concerned about periodically drawing the given world data onto a canvas - it's backbuffer.
 */

public class Renderer {

	private Screen screen;
	private int x = 0;

	public Renderer(Screen screen) {

		this.screen = screen;
		this.screen.setDoubleBuffered(true);
	}

	public void render() {

		System.out.println("rendering");

		Graphics g = screen.getGraphics();
		g.drawRect(x,x,10,10);

		x = ((x+1)%screen.getWidth())%screen.getHeight();

		flip();
	}

	private void flip() {

		screen.repaint();
	}

}
