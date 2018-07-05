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
	}

	public static void createWorld() {

	}

}
