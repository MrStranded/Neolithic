package engine.graphics.gui;

import engine.data.entities.GuiElement;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.math.numericalObjects.Matrix4;

public interface GUIInterface {

	void tick();

	void render(ShaderProgram hudShaderProgram, Matrix4 orthographicMatrix);

	void addElement(GuiElement guiElement);

	void cleanUp();

}
