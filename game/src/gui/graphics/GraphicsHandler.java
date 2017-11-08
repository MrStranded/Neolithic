package gui.graphics;

import gui.WindowInterface;

/**
 * A class containing all needed static variables and methods.
 *
 * Created by michael1337 on 08/11/17.
 */
public class GraphicsHandler {

	private static WindowInterface window;

	public static void addWindow(WindowInterface w) {
		window = w;
	}

	public static void init() {
		if (window != null) window.init();
	}

	public static boolean draw() {
		if (window != null) return window.draw();
		return false;
	}

	public static void tearDown() {
		if (window != null) window.close();
	}

}
