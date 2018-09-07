package engine;

import constants.ScriptConstants;
import engine.data.entities.Instance;
import engine.data.planetary.Planet;
import engine.data.proto.Data;
import engine.graphics.gui.BaseGUI;
import engine.graphics.gui.GUIInterface;
import engine.graphics.objects.Scene;
import engine.graphics.gui.window.Window;
import engine.graphics.renderer.Renderer;
import engine.parser.Parser;

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

	public static void loadData() {
		Parser parser = new Parser();

		Data.initialize();
		parser.load();

		Data.load();
	}

	public static void createWorld() {
		scene = new Scene();
		hud = new BaseGUI();

		gaia = new Planet(32);
		Data.setPlanet(gaia);
		//TopologyGenerator.fitTiles(gaia);

		long time = System.currentTimeMillis();
		gaia.generatePlanetMesh();
		System.out.println("Generating LOD Mesh took: "+(System.currentTimeMillis()-time)+" ms");

		time = System.currentTimeMillis();
		Instance worldGen = new Instance(Data.getContainerID("genContinental"));
		worldGen.runScript(ScriptConstants.EVENT_GENERATE_WORLD, null);
		gaia.updatePlanetMesh();
		System.out.println("Executing WorldGen Script took: "+(System.currentTimeMillis()-time)+" ms");

		/*

		time = System.currentTimeMillis();
		TopologyGenerator.formTopology(gaia);
		System.out.println("Forming Topology took: "+(System.currentTimeMillis()-time)+" ms");

		time = System.currentTimeMillis();
		gaia.updatePlanetMesh();
		System.out.println("Updating LOD Mesh took: "+(System.currentTimeMillis()-time)+" ms");*/

		/*for (int i=0; i<10; i++) {
			Instance i01;
			if (Math.random() < 0.5d) {
				i01 = new Instance(Data.getContainerID("cDuck"));
			} else {
				i01 = new Instance(Data.getContainerID("cTree"));
			}
			i01.setPosition(gaia.getFace((int) (5d*Math.random()), (int) (4d*Math.random())).getTile((int) (32d*Math.random()), (int) (32d*Math.random())));
		}*/
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
		if (renderer != null) { renderer.cleanUp(); }
		if (scene != null) { scene.cleanUp(); }
		if (hud != null) { hud.cleanUp(); }

		Data.clear();

		if (window != null) { window.destroy(); }
	}

}
