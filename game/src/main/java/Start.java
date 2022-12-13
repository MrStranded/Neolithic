import engine.Engine;
import engine.parser.utils.Logger;

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
 * Possibly, you also want to use this:
 * - VM argument: -XX:+UseConcMarkSweepGC
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
 *
 * To use LWJGL Debug (provided the jar is in the lib folder):
 * -javaagent:lib/lwjglx-debug-1.0.0.jar
 */

public class Start {

	public static void main(String[] args) {

		// necessary for AWT and GLFW to run together under OSX
		//System.setProperty("java.awt.headless", "true");

		Logger.setLogLevel(Logger.LOG_TRACE);

		Engine.initialize();
		Engine.loadData();
		Engine.createWorld();

		Logger.setLogLevel(Logger.LOG_DEBUG);

		Engine.startLogic();

		Engine.cleanUp();
	}

}
