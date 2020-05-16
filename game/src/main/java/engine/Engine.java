package engine;

import constants.GameConstants;
import constants.ScriptConstants;
import engine.data.entities.Instance;
import engine.data.options.GameOptions;
import engine.data.planetary.Planet;
import engine.data.Data;
import engine.graphics.gui.GuiData;
import engine.threads.LogicThread;
import engine.parser.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The engine binds the whole thing together.
 * It controls game logic, data and visualisation.
 */

public class Engine {

	private static LogicThread logicThread;

	private static Planet gaia;

	private static List<Task> tasks;

	// ###################################################################################
	// ################################ Setup ############################################
	// ###################################################################################

	private Engine() {}

	public static void initialize() {
		GuiData.initialize();

		logicThread = new LogicThread();

		tasks = new ArrayList<>(Arrays.asList(
				new Task("Updating Planet Mesh", () -> {
					if (Data.shouldUpdatePlanetMesh()) {
						gaia.updatePlanetMesh();
						Data.setUpdatePlanetMesh(false);
					}
				}, 100),

				new Task("Calculating HUD", () -> {
					GuiData.getHud().tick(GuiData.getRenderWindow().getWidth(), GuiData.getRenderWindow().getHeight());
					GuiData.getStatisticsWindow().refresh();
				}, 100),

				new Task("Rendering", () -> {
					GuiData.getRenderer().render(GuiData.getScene(), GuiData.getHud(), gaia);
				}, 100)
		));
	}

	public static void loadData() {
		Data.initialize();

		new Parser().load();

		Data.load();
	}

	public static void createWorld() {
		gaia = new Planet(GameConstants.DEFAULT_PLANET_SIZE);
		Data.setPlanet(gaia);
		//Data.addPlanetTilesToQueue();

		new Task("Generating LOD Mesh", () -> gaia.generatePlanetMesh()).execute();

		new Task("Executing WorldGen Script", () -> {
			Instance worldGen = new Instance(Data.getContainerID("genContinental"));
			worldGen.run(ScriptConstants.EVENT_GENERATE_WORLD, null);
		}).execute();

		Data.shuffleInstanceQueue();
	}

	// ###################################################################################
	// ################################ Running ##########################################
	// ###################################################################################

	public static void startLogic() {
		if (! Engine.logicThread.isAlive()) {
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

			tasks.forEach(Task::execute);

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

	// ###################################################################################
	// ################################ Private Classes ##################################
	// ###################################################################################

	private static class Task {
		String description;
		MeasuredAction action;
		int minTimeForOutput;

		Task(String description, MeasuredAction action) {
			this(description, action, 0);
		}

		Task(String description, MeasuredAction action, int minTimeForOutput) {
			this.description = description;
			this.action = action;
			this.minTimeForOutput = minTimeForOutput;
		}

		void execute() {
			long start = System.currentTimeMillis();

			action.execute();

			if (GameOptions.printPerformance) {
				long dt = (System.currentTimeMillis() - start);
				if (dt >= minTimeForOutput) {
					System.out.println(description + " took: " + dt);
				}
			}
		}
	}

	@FunctionalInterface
	private interface MeasuredAction {
		void execute();
	}

}
