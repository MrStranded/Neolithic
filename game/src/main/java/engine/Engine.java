package engine;

import constants.GameConstants;
import constants.ScriptConstants;
import engine.data.entities.Instance;
import engine.data.planetary.Planet;
import engine.data.Data;
import engine.graphics.gui.GuiData;
import engine.parser.utils.Logger;
import engine.threads.LogicThread;
import engine.parser.Parser;

import java.util.List;

/**
 * The engine binds the whole thing together.
 * It controls game logic, data and visualisation.
 */

public class Engine {

	private static LogicThread logicThread;

	private static Planet gaia;

	private static List<TimedTask> tasks;

	// ###################################################################################
	// ################################ Setup ############################################
	// ###################################################################################

	private Engine() {}

	public static void initialize() {
		GuiData.initialize();

		logicThread = new LogicThread();

		tasks = TimedTask.listOf(
				new TimedTask("Updating Planet Mesh", () -> {
					if (Data.shouldUpdatePlanetMesh()) {
						gaia.updatePlanetMesh();
						Data.setUpdatePlanetMesh(false);
					}
				}, 100),

				new TimedTask("Calculating HUD", () -> {
					GuiData.getHud().tick();
					GuiData.getStatisticsWindow().refresh();
				}, 100),

				new TimedTask("Rendering", () -> {
					GuiData.getRenderer().render(GuiData.getScene(), GuiData.getHud(), gaia);
				}, 100)
		);
	}

	public static void loadData() {
		Logger.info("------------------- Loading Data");

		Data.initialize();
		new Parser().load();

		Data.loadMeshHubs();
	}

	public static void reloadScripts() {
		Logger.info("------------------- Reloading Scripts");

		Data.initializeReload();
		new Parser().load();
		Data.finishReload();

		Data.clearMeshHubs();
		Data.loadMeshHubs();
	}

	public static void createWorld() {
		gaia = new Planet(GameConstants.DEFAULT_PLANET_SIZE);
		Data.setPlanet(gaia);

		new TimedTask("Generating LOD Mesh", () -> gaia.generatePlanetMesh()).execute();

		new TimedTask("Executing WorldGen Script", () -> {
			Instance worldGen = new Instance(Data.getContainerID("genContinental"));
			worldGen.run(ScriptConstants.EVENT_GENERATE_WORLD, null);
		}).execute();

		Data.shuffleInstanceQueue();
	}

	// ###################################################################################
	// ################################ Running ##########################################
	// ###################################################################################

	public static void startLogic() {
		if (! logicThread.isAlive()) {
			new Engine().start();
		}
	}

	/**
	 * Starting the drawing loop and cleaning up the window after exiting the program.
	 * This method cannot be static, because we need the sleep() method from the Thread class.
	 */
	private void start() {
		Engine.logicThread.start();

		while (GuiData.getRenderer().displayExists()) {
			long t = System.currentTimeMillis();

			tasks.forEach(TimedTask::execute);

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

	// ###################################################################################
	// ################################ Clean Up #########################################
	// ###################################################################################

	public static void cleanUp() {
		Data.clear();
		GuiData.clear();
	}
}
