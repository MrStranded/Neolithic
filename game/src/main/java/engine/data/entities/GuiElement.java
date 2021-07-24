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
        Logger.trace("Creating gui object " + getName() + " for template '" + template + "'");

        switch (template) {
            case TEXT:
                String text = getVariableSafe(ScriptConstants.GUI_TEXT).map(Variable::getString).orElse("");
                double xRelPos = getVariableSafe(ScriptConstants.GUI_RELATIVE_X).map(Variable::getDouble).orElse(0d);
                double yRelPos = getVariableSafe(ScriptConstants.GUI_RELATIVE_Y).map(Variable::getDouble).orElse(0d);
                double textSize = getVariableSafe(ScriptConstants.GUI_TEXT_SIZE).map(Variable::getDouble).orElse(GraphicalConstants.DEFAULT_FONT_SIZE);
                RGBA color = getVariableSafe(ScriptConstants.GUI_TEXT_COLOR).map(Variable::getRGBA).orElse(RGBA.WHITE);

                double maxAbsTextObjectWidth = (double) getAbsoluteMaxSelfWidth() / textSize * (double) GuiData.getFontTexture().getHeight();

                TextObject textObject = new TextObject(text, GuiData.getFontTexture(), color, maxAbsTextObjectWidth);
                guiObject = textObject;
                guiObject.setAbsoluteSize(textObject.getTextWidth() * textSize, textObject.getTextHeight() * textSize);
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
        getSubElements().forEach(GuiElement::resize);

        if (guiObject == null) { return; }

        guiObject.recalculateScale(getAbsoluteParentWidth(), getAbsoluteParentHeight());
    }

    // ###################################################################################
    // ################################ Layouting ########################################
    // ###################################################################################

    public void setGuiParent(GuiElement parent) {
        placeInto(parent);
        applyLayouting = true;
    }

    public double getWidth() {
        return guiObject != null ? guiObject.getAbsWidth() : 0;
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
    // ################################ Clean Up #########################################
    // ###################################################################################

    @Override
    public void recursiveSlatingForRemoval() {
        super.recursiveSlatingForRemoval();
        if (guiObject != null) {
            GuiData.rememberToCleanGraphicalObject(guiObject);
            guiObject = null;
        }
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

    public int getAbsoluteParentWidth() {
        GuiElement parent = (GuiElement) getSuperInstance();
        return parent != null
                ? parent.getAbsoluteMaxChildWidth()
                : GuiData.getRenderWindow().getWidth();
    }
    public int getAbsoluteMaxChildWidth() {
        int maxWidth = getAbsoluteMaxSelfWidth();

        boolean limitsWidth = getVariableSafe(ScriptConstants.GUI_LIMITS_WIDTH).map(Variable::getBoolean).orElse(false);
        if (limitsWidth) { maxWidth = (int) Math.min(maxWidth, getWidth()); }

        return maxWidth;
    }
    public int getAbsoluteMaxSelfWidth() {
        int boundedWidth = getAbsoluteParentWidth();

        Variable relMaxWidth = getVariable(ScriptConstants.GUI_RELATIVE_BOUNDING_WIDTH);
        if (relMaxWidth != null) { boundedWidth *= relMaxWidth.getDouble(); }

        return boundedWidth;
    }

    public int getAbsoluteParentHeight() {
        GuiElement parent = (GuiElement) getSuperInstance();
        return parent != null
                ? parent.getAbsoluteMaxChildHeight()
                : GuiData.getRenderWindow().getHeight();
    }
    public int getAbsoluteMaxChildHeight() {
        int maxHeight = getAbsoluteMaxSelfHeight();

        boolean limitsHeigth = getVariableSafe(ScriptConstants.GUI_LIMITS_HEIGHT).map(Variable::getBoolean).orElse(false);
        if (limitsHeigth) { maxHeight = (int) Math.min(maxHeight, getHeight()); }

        return maxHeight;
    }
    public int getAbsoluteMaxSelfHeight() {
        int boundedHeight = getAbsoluteParentHeight();

        Variable relMaxHeight = getVariable(ScriptConstants.GUI_RELATIVE_BOUNDING_HEIGHT);
        if (relMaxHeight != null) { boundedHeight *= relMaxHeight.getDouble(); }

        return boundedHeight;
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
