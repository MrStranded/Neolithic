package engine;

import engine.graphics.window.Window;
import engine.graphics.renderer.Renderer;

/**
 * The engine binds the whole thing together.
 * It controls game logic, data and visualisation.
 */

public class Engine {

	private static Renderer renderer;
	private static Window window;

	public static void initialize() {

		window = new Window(800,600,"Neolithic");

		renderer = new Renderer(window);
		renderer.initialize();
	}

	public static void createWorld() {

	}

	/**
	 * Starting the drawing loop and cleaning up the window after exiting the program.
	 */
	public static void start() {

		renderLoop();
	}

	private static void renderLoop() {

		while (renderer.displayExists()) {
			renderer.render();
		}
	}

	public static void cleanUp() {

		renderer.cleanUp();
	}

}
