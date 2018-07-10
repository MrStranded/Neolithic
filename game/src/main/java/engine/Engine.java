package engine;

import engine.window.Window;
import renderer.Renderer;

/**
 * The engine binds the whole thing together.
 * It controls game logic, data and visualisation.
 */

public class Engine {

	private static Renderer renderer;

	public static void initialize() {

		Window window = new Window(800,600,"Neolithic");

		renderer = new Renderer(window);
		renderer.initialize();
		renderer.setFps(1); // let a brotha chill out
	}

	public static void createWorld() {

	}

	/**
	 * Starting the drawing loop and cleaning up the window after exiting the program.
	 */
	public static void start() {

		while (renderer.displayExists()) {
			renderer.render();
		}
	}

	public static void cleanUp() {

		renderer.cleanUp();
	}

}
