package engine.data.entities;

import constants.GraphicalConstants;
import constants.PropertyKeys;
import constants.ScriptConstants;
import engine.data.options.GameOptions;
import engine.data.variables.Variable;
import engine.graphics.gui.GuiData;
import engine.graphics.gui.GuiTemplates;
import engine.graphics.objects.gui.GUIObject;
import engine.graphics.objects.gui.TextObject;
import engine.graphics.renderer.color.RGBA;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.math.numericalObjects.Matrix4;
import engine.parser.utils.Logger;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collectors;

public class GuiElement extends Instance {

    private GUIObject guiObject;
    private boolean update = false;
    private boolean applyLayouting = false;

    public GuiElement(int id) {
        super(id);
    }

    // ###################################################################################
    // ################################ Logic ############################################
    // ###################################################################################

    @Override
    public void tick() {
        run(ScriptConstants.EVENT_TICK, new Variable[]{});
        getSubElements().forEach(GuiElement::tick);
    }

    // ###################################################################################
    // ################################ Graphical ########################################
    // ###################################################################################

    public void render(ShaderProgram hudShaderProgram, Matrix4 orthographicMatrix) {
        if (isSlatedForRemoval() || GameOptions.reloadScripts) { return; }

        if (update) {
            updateObject();
            update = false;
        }

        // render self
        if (guiObject != null) {
            hudShaderProgram.setUniform("projectionViewMatrix", orthographicMatrix.times(guiObject.getWorldMatrix()));
            guiObject.renderForGUI();
        }

        // render subs
        if (getSubInstances() != null) {
            try {
                getSubInstances().stream()
                        .filter(instance -> instance instanceof GuiElement)
                        .map(GuiElement.class::cast)
                        .forEach(element -> element.render(hudShaderProgram, orthographicMatrix));
            } catch (ConcurrentModificationException e) { /* it's okay really */ }
        }
    }

    // ###################################################################################
    // ################################ Create Gui #######################################
    // ###################################################################################

    private void updateObject() {
        // we should clean up the old mesh
        if (guiObject != null) {
            guiObject.cleanUp();
            guiObject = null;
        }

        Variable templateString = getProperty(PropertyKeys.TEMPLATE);
        if (templateString.notNull()) {
            try {
                GuiTemplates template = GuiTemplates.valueOf(templateString.getString().toUpperCase());
                buildGuiObject(template);

            } catch (IllegalArgumentException e) {
                Logger.error("Could not update Gui mesh. Gui template with name '" + templateString.getString() + "' does not exist!");
            }
        }
    }

    private void buildGuiObject(GuiTemplates template) {
        Logger.debug("Creating gui object for template '" + template + "'");

        switch (template) {
            case TEXT:
                String text = getVariableSafe(ScriptConstants.GUI_TEXT).map(Variable::getString).orElse("");
                double xRelPos = getVariableSafe(ScriptConstants.GUI_RELATIVE_X).map(Variable::getDouble).orElse(0d);
                double yRelPos = getVariableSafe(ScriptConstants.GUI_RELATIVE_Y).map(Variable::getDouble).orElse(0d);
                double textSize = getVariableSafe(ScriptConstants.GUI_TEXT_SIZE).map(Variable::getDouble).orElse(GraphicalConstants.DEFAULT_FONT_SIZE);
                RGBA color = getVariableSafe(ScriptConstants.GUI_TEXT_COLOR).map(Variable::getRGBA).orElse(RGBA.WHITE);

                Logger.trace("Text : '" + text + "'");

                TextObject textObject = new TextObject(text, GuiData.getFontTexture(), color);
                guiObject = textObject;
                guiObject.setAbsoluteSize(textObject.getTextWidth() * textSize, textSize);
                guiObject.setRelativeOffset(xRelPos, yRelPos);

                break;
        }

        applyLayout();

        resize();
    }

    private void applyLayout() {
        if (guiObject == null || !applyLayouting) { return; }

        GuiElement parent = (GuiElement) getSuperInstance();
        if (parent == null) { return; }

        guiObject.setAbsoluteOffset(0, parent.getPositionOfSubElement(this));
    }

    public void resize() {
        if (guiObject == null) { return; }

        guiObject.recalculateScale(getAbsoluteBoundingWidth(), getAbsoluteBoundingHeight());
    }

    // ###################################################################################
    // ################################ Layouting ########################################
    // ###################################################################################

    public void setGuiParent(GuiElement parent) {
        placeInto(parent);
        applyLayouting = true;
    }

    public double getHeight() {
        return getPositionOfSubElement(null);
    }
    private double getPositionOfSubElement(GuiElement element) {
        double height = 0;

        if (guiObject != null) {
            height += guiObject.getAbsHeight();
        }

        for (GuiElement sub : getSubElements()) {
            if (sub == element) { break; }

            height += sub.getPositionOfSubElement(element);
        }

        return height;
    }

    // ###################################################################################
    // ################################ Getters and Setters ##############################
    // ###################################################################################

    @Override
    public void addVariable(Variable variable) {
        super.addVariable(variable);
        update = true;
    }

    /**
     * This will trigger an update of the guiObject of this element and all its children on the next render pass.
     */
    public void shouldUpdate() {
        update = true;
        getSubElements().forEach(GuiElement::shouldUpdate);
    }

    public int getAbsoluteBoundingWidth() {
        Variable relMaxWidth = getVariable(ScriptConstants.GUI_RELATIVE_BOUNDING_WIDTH);

        GuiElement parent = (GuiElement) getSuperInstance();
        int parentWidth = parent != null ? parent.getAbsoluteBoundingWidth() : GuiData.getRenderWindow().getWidth();

        return relMaxWidth == null ? parentWidth : (int) (parentWidth * relMaxWidth.getDouble());
    }
    public int getAbsoluteBoundingHeight() {
        Variable relMaxHeight = getVariable(ScriptConstants.GUI_RELATIVE_BOUNDING_HEIGHT);

        GuiElement parent = (GuiElement) getSuperInstance();
        int parentHeight = parent != null ? parent.getAbsoluteBoundingHeight() : GuiData.getRenderWindow().getHeight();

        return relMaxHeight == null ? parentHeight : (int) (parentHeight * relMaxHeight.getDouble());
    }

    public List<GuiElement> getSubElements() {
        if (getSubInstances() == null) { return Collections.emptyList(); }

        return getSubInstances().stream()
                .filter(instance -> instance instanceof GuiElement)
                .map(GuiElement.class::cast)
                .collect(Collectors.toList());
    }

    // ###################################################################################
    // ################################ Debugging ########################################
    // ###################################################################################

    public String toString() {
        return "Gui Element (id = " + id + (getName() != null ? " ,name = " + getName() : "") + ")";
    }
}
