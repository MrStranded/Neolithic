package engine.graphics.gui;

import engine.data.entities.GuiElement;
import engine.data.entities.Instance;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.math.numericalObjects.Matrix4;

import java.util.ArrayList;
import java.util.List;

public class BaseGUI implements GUIInterface {

	private List<GuiElement> elements;

	public BaseGUI() {
		elements = new ArrayList<>();
	}

	// ###################################################################################
	// ################################ Functionality ####################################
	// ###################################################################################

	public void tick() {

	}

	// ###################################################################################
	// ################################ Graphical ########################################
	// ###################################################################################

	public void render(ShaderProgram hudShaderProgram, Matrix4 orthographicMatrix) {
		for (GuiElement element : elements) {
			element.render(hudShaderProgram, orthographicMatrix);
		}
	}

	// ###################################################################################
	// ################################ Accessing ########################################
	// ###################################################################################

	public void addElement(GuiElement element) {
		elements.forEach(Instance::destroy);
		elements.clear();
		elements.add(element);
	}

	// ###################################################################################
	// ################################ Clean Up #########################################
	// ###################################################################################

	public void cleanUp() {
		for (GuiElement element : elements) {
			element.destroy();
		}
	}


	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

}
