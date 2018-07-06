package engine;

import engine.window.Window;
import renderer.RenderThread;
import renderer.Renderer;

/**
 * The engine binds the whole thing together.
 * It controls game logic, data and visualisation.
 */

public class Engine {

	private static Renderer renderer;

	public static void initialize() {

		Window window = new Window(800,600,"Neolithic");

		renderer = new Renderer(window.getScreen());
		RenderThread renderThread = new RenderThread(renderer);

		renderThread.start();
	}

	public static void createWorld() {

	}

}
