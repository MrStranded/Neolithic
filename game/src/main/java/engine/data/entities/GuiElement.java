package engine.data.entities;

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

        if (guiObject == null && text != null) {
            Logger.trace("Creating guiObject with text '" + text + "'");

            guiObject = new TextObject(text, GuiData.getFontTexture());
            guiObject.setSize(600,30);
            guiObject.setLocation(0,0);
            guiObject.setRelativeScreenPositionX(RelativeScreenPosition.CENTER);
        }

        // render self
        if (guiObject != null) {
            hudShaderProgram.setUniform("projectionViewMatrix", orthographicMatrix.times(guiObject.getWorldMatrix()));
            guiObject.renderForGUI();
        }

        // render subs
        if (getSubInstances() != null) {
            try {
                getSubInstances().forEach(Instance::render);
            } catch (ConcurrentModificationException e) { /* it's okay really */ }
        }
    }

    protected void recursiveSlatingForRemoval() {
        super.recursiveSlatingForRemoval();

        if (guiObject != null) {
            guiObject.cleanUp();
        }
    }

    // ###################################################################################
    // ################################ Getters and Setters ##############################
    // ###################################################################################

    private String text;

    public void setText(String text) {
        this.text = text;

        if (guiObject != null) {
            guiObject.cleanUp();
            guiObject = null;
        }
    }

    // ###################################################################################
    // ################################ Debugging ########################################
    // ###################################################################################

    public String toString() {
        return "Gui Element (id = " + id + (getName() != null ? " ,name = " + getName() : "") + ")";
    }
}
