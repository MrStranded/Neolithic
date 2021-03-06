package engine.data.options;

import engine.data.entities.Instance;

public class GameOptions {

	public static boolean printPerformance = true;
	public static boolean plotEntities = true;
	public static boolean plotOnlySelectedEntity = false;

	public static boolean runTicks = false;
	public static boolean stopAtNextTick = false;

	public static boolean reloadScripts = false;

	public static int currentContainerId = 0;
	public static Instance selectedInstance = null;

}
