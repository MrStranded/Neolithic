package gui.graphics;

import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

/**
 * Created by michael1337 on 08/11/17.
 */
public class GraphicsManipulator {

	long window;

	public GraphicsManipulator() {
		window = GLFW.glfwCreateWindow(0,0,"",0,0);
		glfwMakeContextCurrent(window);
	}

	public void clear() {
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
	}

	// ###################################################################################
	// ################################ Object Manipulation ##############################
	// ###################################################################################

}
