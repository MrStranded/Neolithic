package renderer;

import engine.window.Screen;
import renderer.color.RGBA;
import renderer.image.Image;

import javax.swing.*;
import java.awt.*;

/**
 * The renderer is only concerned about periodically drawing the given world data onto a screen - it's backbuffer.
 */

public class Renderer {

	private Screen screen;
	private int x = 0;

	public Renderer(Screen screen) {

		this.screen = screen;
	}

	public void render() {

		System.out.println("rendering");

		Graphics g = screen.getBufferStrategy().getDrawGraphics();

		g.clearRect(0,0,screen.getWidth(),screen.getHeight());

		g.drawRect(x,x,10,10);

		x = ((x+1)%(screen.getWidth()-10))%(screen.getHeight()-10);

		screen.drawTestImage(g);

		flip();
	}

	private void flip() {

		screen.getBufferStrategy().show();
	}

}
