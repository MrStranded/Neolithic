package engine.data.entities;

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

import java.util.ConcurrentModificationException;

public class GuiElement extends Instance {

    private GUIObject guiObject;
    private boolean update = false;

    public GuiElement(int id) {
        super(id);
    }

    // ###################################################################################
    // ################################ Logic ############################################
    // ###################################################################################

    @Override
    public void tick() {
        run(ScriptConstants.EVENT_TICK, new Variable[]{});
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
                        .map(instance -> (GuiElement) instance)
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
                createGuiForTemplate(template);

            } catch (IllegalArgumentException e) {
                Logger.error("Could not update Gui mesh. Gui template with name '" + templateString.getString() + "' does not exist!");
            }
        }
    }

    private void createGuiForTemplate(GuiTemplates template) {
        Logger.trace("Creating gui object for template '" + template + "'");

        switch (template) {
            case TEXT:
                String text = getVariableSafe(ScriptConstants.GUI_TEXT).map(Variable::getString).orElse("");
                double xRelPos = getVariableSafe(ScriptConstants.GUI_RELATIVE_X).map(Variable::getDouble).orElse(0d);
                double yRelPos = getVariableSafe(ScriptConstants.GUI_RELATIVE_Y).map(Variable::getDouble).orElse(0d);
                double textSize = getVariableSafe(ScriptConstants.GUI_TEXT_SIZE).map(Variable::getDouble).orElse(20d);
                RGBA color = getVariableSafe(ScriptConstants.GUI_TEXT_COLOR).map(Variable::getRGBA).orElse(RGBA.WHITE);

                TextObject textObject = new TextObject(text, GuiData.getFontTexture(), color);
                guiObject = textObject;
                guiObject.setAbsoluteSize(textObject.getTextWidth() * textSize, textSize);
                guiObject.setRelativeLocation(xRelPos, yRelPos);

                break;
        }

        resize();
    }

    public void resize() {
        if (guiObject == null) { return; }
        guiObject.recalculateScale(getAbsoluteBoundingWidth(), getAbsoluteBoundingHeight());
    }

    // ###################################################################################
    // ################################ Getters and Setters ##############################
    // ###################################################################################

    @Override
    public void addVariable(Variable variable) {
        super.addVariable(variable);
        update = true;
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

    // ###################################################################################
    // ################################ Debugging ########################################
    // ###################################################################################

    public String toString() {
        return "Gui Element (id = " + id + (getName() != null ? " ,name = " + getName() : "") + ")";
    }
}
