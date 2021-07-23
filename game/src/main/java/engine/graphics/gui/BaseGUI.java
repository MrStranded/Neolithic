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

	@Override
	public void tick() {
		elements.forEach(GuiElement::tick);
	}

	// ###################################################################################
	// ################################ Graphical ########################################
	// ###################################################################################

	@Override
	public void render(ShaderProgram hudShaderProgram, Matrix4 orthographicMatrix) {
		elements.forEach(e -> e.render(hudShaderProgram, orthographicMatrix));
	}

	@Override
	public void resize() {
		elements.forEach(GuiElement::resize);
	}

	// ###################################################################################
	// ################################ Accessing ########################################
	// ###################################################################################

	@Override
	public void addElement(GuiElement element) {
		elements.add(element);
	}

	// ###################################################################################
	// ################################ Clean Up #########################################
	// ###################################################################################

	@Override
	public void clear() {
		elements.forEach(Instance::destroy);
		elements.clear();
	}


	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

}
