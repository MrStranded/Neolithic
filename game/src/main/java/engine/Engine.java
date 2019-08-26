package engine;

import constants.GameConstants;
import engine.data.options.GameOptions;
import engine.data.planetary.Planet;
import engine.data.Data;
import engine.graphics.gui.GuiData;
import engine.threads.LogicThread;
import engine.parser.Parser;

/**
 * The engine binds the whole thing together.
 * It controls game logic, data and visualisation.
 */

public class Engine {

	private static LogicThread logicThread;

	private static Planet gaia;

	public static void initialize() {
		GuiData.initialize();

		logicThread = new LogicThread();
	}

	public static void loadData() {
		Parser parser = new Parser();

		Data.initialize();
		parser.load();

		Data.load();
	}

	public static void createWorld() {
		gaia = new Planet(GameConstants.DEFAULT_PLANET_SIZE);
		Data.setPlanet(gaia);
		//Data.addPlanetTilesToQueue();

		long time = System.currentTimeMillis();
		gaia.generatePlanetMesh();
		System.out.println("Generating LOD Mesh took: "+(System.currentTimeMillis()-time)+" ms");

		time = System.currentTimeMillis();
		/*Instance worldGen = new Instance(Data.getContainerID("genContinental"));
		worldGen.run(ScriptConstants.EVENT_GENERATE_WORLD, null);*/
		System.out.println("Executing WorldGen Script took: "+(System.currentTimeMillis()-time)+" ms");

		Data.shuffleInstanceQueue();
	}

	/**
	 * Starting the drawing loop and cleaning up the window after exiting the program.
	 * This method cannot be static, because we need the sleep() method from the Thread class.
	 */
	public void start() {
		Engine.logicThread.start();

		while (GuiData.getRenderer().displayExists()) {
			long t = System.currentTimeMillis();

			/*Queue<Tile> tiles = Data.getChangedTiles();
			if (!tiles.isEmpty()) {
				gaia.updatePlanetMesh(tiles.poll());
			}*/

			long start = System.currentTimeMillis();
			if (Data.shouldUpdatePlanetMesh()) {
				gaia.updatePlanetMesh();
				Data.updateInstancePositions();
				Data.setUpdatePlanetMesh(false);
				gaia.clearChangeFlags();
			}
			if (GameOptions.printPerformance) {
				long dt = (System.currentTimeMillis() - start);
				if (dt > 100) {
					System.out.println("Updating Planet Mesh took: " + dt);
				}
			}

			start = System.currentTimeMillis();
			GuiData.getHud().tick(GuiData.getRenderWindow().getWidth(), GuiData.getRenderWindow().getHeight());
			GuiData.getStatisticsWindow().refresh();
			if (GameOptions.printPerformance) {
				long dt = (System.currentTimeMillis() - start);
				if (dt > 100) {
					System.out.println("Calculating HUD took: " + dt);
				}
			}

			start = System.currentTimeMillis();
			GuiData.getRenderer().render(GuiData.getScene(), GuiData.getHud(), gaia);
			if (GameOptions.printPerformance) {
				long dt = (System.currentTimeMillis() - start);
				if (dt > 100) {
					System.out.println("Rendering took: " + dt);
				}
			}

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
		Data.clear();
		GuiData.clear();
	}

}
