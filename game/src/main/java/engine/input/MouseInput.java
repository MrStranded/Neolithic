package engine.input;

import engine.window.Window;
import org.lwjgl.glfw.GLFW;

public class MouseInput {

	private double xPos = 0;
	private double yPos = 0;
	private double zPos = 0; // scrolling wheel

	private double xSpeed = 0;
	private double ySpeed = 0;
	private double zSpeed = 0; // scrolling wheel speed

	private boolean inWindow = false;

	private boolean leftButtonPressed = false;
	private boolean rightButtonPressed = false;

	public MouseInput(Window window) {
		initialize(window);
	}

	private void initialize(Window window) {

		GLFW.glfwSetCursorPosCallback(window.getWindowId(), (windowHandle, newXPos, newYPos) -> {
			xSpeed = newXPos - xPos;
			ySpeed = newYPos - yPos;
			xPos = newXPos;
			yPos = newYPos;
		});
		GLFW.glfwSetScrollCallback(window.getWindowId(), (windowHandle, xOffset, yOffset) -> {
			zSpeed = yOffset;
			zPos += zSpeed;
		});
		GLFW.glfwSetCursorEnterCallback(window.getWindowId(), (windowHandle, entered) -> {
			inWindow = entered;
		});
		GLFW.glfwSetMouseButtonCallback(window.getWindowId(), (windowHandle, button, action, mode) -> {
			leftButtonPressed = (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS);
			rightButtonPressed = (button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS);
		});
	}

	public String toString() {
		return "xPos: " + xPos + " ,yPos: " + yPos + " ,zPos: " + zPos;
	}
}
