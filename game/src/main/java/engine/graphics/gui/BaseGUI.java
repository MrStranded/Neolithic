package engine.graphics.gui;

import engine.data.entities.GuiElement;
import engine.data.entities.Instance;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.input.MouseInput;
import engine.math.numericalObjects.Matrix4;
import engine.parser.utils.Logger;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;

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
		try {
			elements.forEach(GuiElement::tick);
		} catch (ConcurrentModificationException e) {
			Logger.error("ConcurrentModificationException during tick of sub gui elements. " +
					"\nProbable cause is that a sub element cleared gui elements during tick script.");
		}
	}

	@Override
	public GuiElement getElementUnderMouse(double mouseX, double mouseY) {
		return elements.stream()
				.map(element -> element.getElementUnderMouse(mouseX, mouseY))
				.filter(Objects::nonNull)
				.findFirst().orElse(null);
	}

	// ###################################################################################
	// ################################ Graphical ########################################
	// ###################################################################################

	@Override
	public void render(ShaderProgram hudShaderProgram, Matrix4 orthographicMatrix, MouseInput mouse) {
		elements.forEach(e -> e.render(hudShaderProgram, orthographicMatrix, mouse));
	}

	@Override
	public void resize() {
		Logger.info("Screen has been resized");
		elements.forEach(GuiElement::consolidate);
	}

	public void updateGui(MouseInput mouse) {
		elements.forEach(element -> element.updateBackgrounds(mouse));
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
	// ################################ Debug ############################################
	// ###################################################################################

	@Override
	public void debug() {
		elements.forEach(element -> element.debug("-   "));
	}

}
