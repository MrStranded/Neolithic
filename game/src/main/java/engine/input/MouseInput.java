package engine.input;

import engine.graphics.gui.window.Window;
import org.lwjgl.glfw.GLFW;

public class MouseInput {

	private boolean stateChanged = false;

	private double xPos = 0;
	private double yPos = 0;
	private double zPos = 0; // scrolling wheel

	private double xSpeed = 0;
	private double ySpeed = 0;
	private double zSpeed = 0; // scrolling wheel speed

	private boolean inWindow = false;

	private boolean leftButtonPressed = false;
	private boolean rightButtonPressed = false;
	private boolean leftButtonClicked = false;
	private boolean rightButtonClicked = false;

	public MouseInput(Window window) {
		initialize(window);
	}

	private void initialize(Window window) {
		GLFW.glfwSetCursorPosCallback(window.getWindowId(), (windowHandle, newXPos, newYPos) -> {
			xSpeed = newXPos - xPos;
			ySpeed = newYPos - yPos;
			xPos = newXPos;
			yPos = newYPos;
			stateChanged = true;
		});
		GLFW.glfwSetScrollCallback(window.getWindowId(), (windowHandle, xOffset, yOffset) -> {
			zSpeed = yOffset;
			zPos += zSpeed;
			stateChanged = true;
		});
		GLFW.glfwSetCursorEnterCallback(window.getWindowId(), (windowHandle, entered) -> inWindow = entered);
		GLFW.glfwSetMouseButtonCallback(window.getWindowId(), (windowHandle, button, action, mode) -> {
			boolean leftPressedBefore = leftButtonPressed;
			boolean rightPressedBefore = rightButtonPressed;
			leftButtonPressed = (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS);
			rightButtonPressed = (button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS);
			leftButtonClicked = (leftButtonPressed && !leftPressedBefore) || leftButtonClicked;
			rightButtonClicked = (rightButtonPressed && !rightPressedBefore) || rightButtonClicked;
			stateChanged = true;
		});
	}

	// ###################################################################################
	// ################################ Flush ############################################
	// ###################################################################################

	public void flush() {
		leftButtonClicked = false;
		rightButtonClicked = false;
		stateChanged = false;

		xSpeed = ySpeed = zSpeed = 0;
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public boolean hasStateChanged() {
		return stateChanged;
	}

	public double getXPos() {
		return xPos;
	}

	public double getYPos() {
		return yPos;
	}

	public double getZPos() {
		return zPos;
	}

	public double getXSpeed() {
		return xSpeed;
	}

	public double getYSpeed() {
		return ySpeed;
	}

	public double getZSpeed() {
		return zSpeed;
	}

	public boolean isInWindow() {
		return inWindow;
	}

	public boolean isLeftButtonPressed() {
		return leftButtonPressed;
	}

	public boolean isRightButtonPressed() {
		return rightButtonPressed;
	}

	public boolean isLeftButtonClicked() {
		return leftButtonClicked;
	}

	public boolean isRightButtonClicked() {
		return rightButtonClicked;
	}

	// ###################################################################################
	// ################################ Debugging ########################################
	// ###################################################################################

	public String toString() {
		return "xPos: " + xPos + " ,yPos: " + yPos + " ,zPos: " + zPos + " ,inWindow: " + inWindow;
	}
}
