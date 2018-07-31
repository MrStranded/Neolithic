import engine.Engine;

/**
 * Use this class to start the game.
 * It sets up the top-most services.
 *
 * OpenGL version: 3.0 Mesa 17.2.8
 * LWJGL version: 3.1.6
 *
 * Running the program:
 * - go sure to use this VM argument: -Dsun.java2d.opengl=true
 *
 * On Windows, this other option may improve performance and stability:
 * -Dsun.java2d.noddraw=true
 *
 * To increase Heap Size (crucial):
 * -Xms<size>        set initial Java heap size
 * -Xmx<size>        set maximum Java heap size
 * -Xss<size>        set java thread stack size
 *
 * java -Xms1024m -Xmx2048m Start
 */

public class Start {

	public static void main(String[] args) {

		Engine.initialize();
		Engine.createWorld();

		Engine.start();

		Engine.cleanUp();
	}

}
