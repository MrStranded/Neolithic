package engine.input;

import engine.graphics.window.Window;
import org.lwjgl.glfw.GLFW;

public class KeyboardInput {

	private static final int KEYLIMIT = 512;

	private boolean keyPressed[] = new boolean[KEYLIMIT];
	private boolean keyClicked[] = new boolean[KEYLIMIT];

	public KeyboardInput(Window window) {
		initialize(window);
	}

	private void initialize(Window window) {

		GLFW.glfwSetKeyCallback(window.getWindowId(), (windowHandle, key, scancode, action, mods) -> {
			if (key >= 0 && key < KEYLIMIT) {
				if (action == GLFW.GLFW_PRESS) {
					keyPressed[key] = true;
				} else if (action == GLFW.GLFW_RELEASE) {
					keyPressed[key] = false;
					keyClicked[key] = true;
				}
			}
		});
	}

	public boolean isPressed(int key) {
		if (key >= 0 && key < KEYLIMIT) {
			return keyPressed[key];
		}
		return false;
	}

	public boolean isClicked(int key) {
		if (key >= 0 && key < KEYLIMIT) {
			boolean clicked = keyClicked[key];
			keyClicked[key] = false;
			return clicked;
		}
		return false;
	}
}
