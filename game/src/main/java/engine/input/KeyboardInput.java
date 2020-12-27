package engine.input;

import engine.graphics.gui.window.Window;
import org.lwjgl.glfw.GLFW;

public class KeyboardInput {

	private static final int KEY_LIMIT = 512;

	private boolean keyPressed[] = new boolean[KEY_LIMIT];
	private boolean keyClicked[] = new boolean[KEY_LIMIT];

	public KeyboardInput(Window window) {
		initialize(window);
	}

	private void initialize(Window window) {

		GLFW.glfwSetKeyCallback(window.getWindowId(), (windowHandle, key, scancode, action, mods) -> {
			if (key >= 0 && key < KEY_LIMIT) {
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
		if (key >= 0 && key < KEY_LIMIT) {
			return keyPressed[key];
		}
		return false;
	}

	public boolean isClicked(int key) {
		if (key >= 0 && key < KEY_LIMIT) {
			boolean clicked = keyClicked[key];
			keyClicked[key] = false;
			return clicked;
		}
		return false;
	}

	public boolean ctrlPressed() {
		return keyPressed[GLFW.GLFW_KEY_LEFT_CONTROL] || keyPressed[GLFW.GLFW_KEY_RIGHT_CONTROL];
	}
	public boolean ctrlPressed(int key) {
		return ctrlPressed() && isClicked(key);
	}

}
