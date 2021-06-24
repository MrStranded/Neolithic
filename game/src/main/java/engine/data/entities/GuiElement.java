package engine.data.entities;

import constants.PropertyKeys;
import constants.ScriptConstants;
import engine.data.Data;
import engine.data.options.GameOptions;
import engine.data.proto.Container;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.graphics.gui.GuiData;
import engine.graphics.gui.RelativeScreenPosition;
import engine.graphics.objects.MeshHub;
import engine.graphics.objects.gui.GUIObject;
import engine.graphics.objects.gui.TextObject;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.math.numericalObjects.Matrix4;
import engine.parser.utils.Logger;

import java.util.ConcurrentModificationException;
import java.util.Optional;

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
//        if (guiObject != null) {
//            guiObject.cleanUp();
//            guiObject = null;
//        }

        Variable template = getProperty(PropertyKeys.TEMPLATE);

        if (template.notNull()) { createGuiForTemplate(template.getString().toLowerCase()); }
    }

    private void createGuiForTemplate(String template) {
        Logger.trace("Creating gui object for template '" + template + "'");

        if ("text".equals(template)) {
            String text = getProperty(PropertyKeys.TEXT).getString();

            guiObject = new TextObject(text, GuiData.getFontTexture());
            guiObject.setSize(text.length() * 10, 20);
            guiObject.setLocation(-400, 0);
            guiObject.setRelativeScreenPositionX(RelativeScreenPosition.CENTER);
        }
    }

    // ###################################################################################
    // ################################ Getters and Setters ##############################
    // ###################################################################################

    public void setText(Variable text) {
        getContainer().ifPresent(container -> container.setProperty(
                getStage(),
                PropertyKeys.TEXT.key(),
                text
        ));

        update = true;
    }

    // ###################################################################################
    // ################################ Debugging ########################################
    // ###################################################################################

    public String toString() {
        return "Gui Element (id = " + id + (getName() != null ? " ,name = " + getName() : "") + ")";
    }
}
