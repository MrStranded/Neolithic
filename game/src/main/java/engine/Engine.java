package engine;

import constants.GameConstants;
import constants.ScriptConstants;
import engine.data.entities.Instance;
import engine.data.planetary.Planet;
import engine.data.Data;
import engine.data.planetary.Tile;
import engine.graphics.gui.BaseGUI;
import engine.graphics.gui.GUIInterface;
import engine.graphics.objects.Scene;
import engine.graphics.gui.window.Window;
import engine.graphics.renderer.Renderer;
import engine.threads.LogicThread;
import engine.parser.Parser;

import java.util.Queue;

/**
 * The engine binds the whole thing together.
 * It controls game logic, data and visualisation.
 */

public class Engine {

	private static Renderer renderer;
	private static Window window;
	private static Scene scene;
	private static GUIInterface hud;

	private static LogicThread logicThread;

	private static Planet gaia;

	public static void initialize() {
		window = new Window(800,600,"Neolithic");
		window.initialize();

		renderer = new Renderer(window);
		renderer.initialize();

		logicThread = new LogicThread(window);
	}

	public static void loadData() {
		Parser parser = new Parser();

		Data.initialize();
		parser.load();

		Data.load();
	}

	public static void createWorld() {
		scene = new Scene();
		hud = new BaseGUI();

		gaia = new Planet(GameConstants.DEFAULT_PLANET_SIZE);
		Data.setPlanet(gaia);
		Data.addPlanetTilesToQueue();

		long time = System.currentTimeMillis();
		gaia.generatePlanetMesh();
		System.out.println("Generating LOD Mesh took: "+(System.currentTimeMillis()-time)+" ms");

		time = System.currentTimeMillis();
		Instance worldGen = new Instance(Data.getContainerID("genContinental"));
		worldGen.run(ScriptConstants.EVENT_GENERATE_WORLD, null);
		gaia.updatePlanetMesh();
		Data.updateInstancePositions();
		System.out.println("Executing WorldGen Script took: "+(System.currentTimeMillis()-time)+" ms");

		Data.shuffleInstanceQueue();
	}

	/**
	 * Starting the drawing loop and cleaning up the window after exiting the program.
	 * This method cannot be static, because we need the sleep() method from the Thread class.
	 */
	public void start() {
		Engine.logicThread.start();

		while (renderer.displayExists()) {
			long t = System.currentTimeMillis();

			Queue<Tile> tiles = Data.getChangedTiles();
			if (!tiles.isEmpty()) {
				gaia.updatePlanetMesh(tiles.poll());
			}

			long start = System.currentTimeMillis();
			//hud.tick(window.getWidth(), window.getHeight());
			//System.out.println("Calculating HUD took: " + (System.currentTimeMillis() - start) + " ms");

			start = System.currentTimeMillis();
			renderer.render(scene, hud, gaia);
			//System.out.println("Rendering took: " + (System.currentTimeMillis() - start) + " ms");

			long elapsedTime = System.currentTimeMillis() - t;
			if (elapsedTime < GameConstants.MILLISECONDS_PER_FRAME) {
				try {
					Thread.sleep(GameConstants.MILLISECONDS_PER_FRAME - elapsedTime);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	public static void cleanUp() {
		if (renderer != null) { renderer.cleanUp(); }
		if (scene != null) { scene.cleanUp(); }
		if (hud != null) { hud.cleanUp(); }

		Data.clear();

		if (window != null) { window.destroy(); }
	}

}
