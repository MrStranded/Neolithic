package engine.graphics.gui;

import engine.data.entities.GuiElement;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.input.MouseInput;
import engine.math.numericalObjects.Matrix4;

public interface GUIInterface {

	void tick();

	void render(ShaderProgram hudShaderProgram, Matrix4 orthographicMatrix);

	void resize();

	void addElement(GuiElement guiElement);

	GuiElement getElementUnderMouse(double mouseX, double mouseY);

	void clear();

	void debug();

	void updateGui(MouseInput mouse);

}
