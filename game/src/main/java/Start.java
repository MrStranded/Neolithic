import engine.Engine;

/**
 * Use this class to start the game.
 * It sets up the top-most services.
 *
 * Running the program:
 * - go sure to use this VM argument: -Dsun.java2d.opengl=true
 */

public class Start {

	public static void main(String[] args) {

		Engine engine = new Engine();
		engine.loadData();
		engine.createWorld();

	}

}
