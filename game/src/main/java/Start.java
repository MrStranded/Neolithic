import engine.Engine;
import math.Matrix4;

/**
 * Use this class to start the game.
 * It sets up the top-most services.
 *
 * OpenGL version: 3.0 Mesa 17.2.8
 * LWJGL version: 2.9.3
 *
 * Running the program:
 * - go sure to use this VM argument: -Dsun.java2d.opengl=true
 *
 * On Windows, this other option may improve performance and stability:
 * -Dsun.java2d.noddraw=true
 */

public class Start {

	public static void main(String[] args) {

		Engine.initialize();
		Engine.createWorld();

		Engine.start();

		Engine.cleanUp();
	}

}
