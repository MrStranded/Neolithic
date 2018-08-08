package engine;

import engine.data.Planet;
import engine.graphics.gui.BaseGUI;
import engine.graphics.gui.GUIInterface;
import engine.graphics.objects.Scene;
import engine.graphics.gui.window.Window;
import engine.graphics.renderer.Renderer;
import engine.logic.TopologyGenerator;

/**
 * The engine binds the whole thing together.
 * It controls game logic, data and visualisation.
 */

public class Engine {

	private static Renderer renderer;
	private static Window window;
	private static Scene scene;
	private static GUIInterface hud;

	private static Planet gaia;

	public static void initialize() {
		window = new Window(800,600,"Neolithic");

		renderer = new Renderer(window);
		renderer.initialize();
	}

	public static void createWorld() {
		scene = new Scene();
		hud = new BaseGUI();

		gaia = new Planet(32);

		long time = System.currentTimeMillis();
		gaia.generatePlanetMesh();
		System.out.println("Generating LOD Mesh took: "+(System.currentTimeMillis()-time)+" ms");

		time = System.currentTimeMillis();
		TopologyGenerator.formTopology(gaia);
		System.out.println("Forming Topology took: "+(System.currentTimeMillis()-time)+" ms");

		time = System.currentTimeMillis();
		gaia.updatePlanetMesh();
		System.out.println("Updating LOD Mesh took: "+(System.currentTimeMillis()-time)+" ms");
	}

	/**
	 * Starting the drawing loop and cleaning up the window after exiting the program.
	 */
	public static void start() {
		renderLoop();
	}

	private static void renderLoop() {
		while (renderer.displayExists()) {
			renderer.render(scene, hud, gaia);
		}
	}

	public static void cleanUp() {
		renderer.cleanUp();
		scene.cleanUp();
		hud.cleanUp();

		window.destroy();
	}

}
