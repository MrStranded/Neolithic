package engine.data.entities;

import constants.PropertyKeys;
import constants.ScriptConstants;
import engine.data.options.GameOptions;
import engine.data.variables.Variable;
import engine.graphics.gui.GuiData;
import engine.graphics.gui.GuiTemplates;
import engine.graphics.objects.GraphicalObject;
import engine.graphics.objects.gui.GuiObject;
import engine.graphics.objects.gui.TemplateProcessor;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.math.numericalObjects.Matrix4;
import engine.parser.utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GuiElement extends Instance {

    private List<GuiObject> guiObjects = new ArrayList<>(1);
    private boolean update = false;
    private boolean applyLayouting = false;
    private boolean rendering = false;

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
        if (isSlatedForRemoval() || GameOptions.reloadScripts) {
            return;
        }

        rendering = true;

        // there were updates
        if (update) {
            updateObject();
            update = false;
        }

        // render self
        guiObjects.forEach(object -> {
            hudShaderProgram.setUniform("projectionViewMatrix", orthographicMatrix.times(object.getWorldMatrix()));
            object.renderForGUI();
        });

        // render subs
        getSubElements().forEach(element -> element.render(hudShaderProgram, orthographicMatrix));

        rendering = false;
    }

    // ###################################################################################
    // ################################ Create Gui #######################################
    // ###################################################################################

    private void updateObject() {
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
        setGuiObjects(
                TemplateProcessor.create(this, template)
        );

        applyLayout();

        resize();
    }

    private void applyLayout() {
        if (!applyLayouting) {
            return;
        }

        GuiElement parent = (GuiElement) getSuperInstance();
        if (parent == null) {
            return;
        }

        guiObjects.forEach(object -> object.setAbsoluteOffset(0, parent.getPositionOfSubElement(this)));
    }

    public void resize() {
        getSubElements().forEach(GuiElement::resize);

        guiObjects.forEach(object -> object.resize(this));
        guiObjects.forEach(object -> object.recalculateScale(getAbsoluteParentWidth(), getAbsoluteParentHeight()));
    }

    // ###################################################################################
    // ################################ Layouting ########################################
    // ###################################################################################

    public void setGuiParent(GuiElement parent) {
        placeInto(parent);
        applyLayouting = true;
    }

    public double getAbsWidth() {
        return guiObjects.stream()
                .filter(GuiObject::isInfluenceSizeCalculations)
                .max((a, b) -> (int) Math.signum(a.getAbsWidth() - b.getAbsWidth()))
                .map(GuiObject::getAbsWidth)
                .orElse(0d);
    }

    public double getAbsHeight() {
        return guiObjects.stream()
                .filter(GuiObject::isInfluenceSizeCalculations)
                .max((a, b) -> (int) Math.signum(a.getAbsHeight() - b.getAbsHeight()))
                .map(GuiObject::getAbsHeight)
                .orElse(0d);
    }

    public double getWidth() {
        double maxWidth = getAbsWidth();

        for (GuiElement sub : getSubElements()) {
            double width = sub.getWidth();
            if (width > maxWidth) { maxWidth = width; }
        }

        return maxWidth;
    }

    public double getHeight() {
        return getPositionOfSubElement(null);
    }

    private double getPositionOfSubElement(GuiElement element) {
        double height = getAbsHeight();

        for (GuiElement sub : getSubElements()) {
            if (sub == element) {
                break;
            }

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
        GuiData.rememberToCleanGuiElement(this);
    }

    public void clearGuiObject() {
        guiObjects.forEach(GraphicalObject::cleanUp);
        guiObjects.clear();
    }

    // ###################################################################################
    // ################################ Getters and Setters ##############################
    // ###################################################################################

    @Override
    public void addVariable(Variable variable) {
        super.addVariable(variable);
        update = true;
    }

    private void setGuiObjects(GuiObject... newObjects) {
        guiObjects.forEach(GraphicalObject::cleanUp);
        guiObjects.clear();
        guiObjects.addAll(Arrays.asList(newObjects));
    }

    /**
     * This will trigger an update of the guiObject of this element and all its children on the next render pass.
     */
    public void shouldUpdate() {
        update = true;
        getSubElements().forEach(GuiElement::shouldUpdate);
    }

    public boolean isRendering() {
        return rendering;
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
        if (limitsWidth) {
            maxWidth = (int) Math.min(maxWidth, getAbsWidth());
        }

        return maxWidth;
    }

    public int getAbsoluteMaxSelfWidth() {
        int boundedWidth = getAbsoluteParentWidth();

        Variable relMaxWidth = getVariable(ScriptConstants.GUI_RELATIVE_BOUNDING_WIDTH);
        if (relMaxWidth != null) {
            boundedWidth *= relMaxWidth.getDouble();
        }

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
        if (limitsHeigth) {
            maxHeight = (int) Math.min(maxHeight, getHeight());
        }

        return maxHeight;
    }

    public int getAbsoluteMaxSelfHeight() {
        int boundedHeight = getAbsoluteParentHeight();

        Variable relMaxHeight = getVariable(ScriptConstants.GUI_RELATIVE_BOUNDING_HEIGHT);
        if (relMaxHeight != null) {
            boundedHeight *= relMaxHeight.getDouble();
        }

        return boundedHeight;
    }

    public List<GuiElement> getSubElements() {
        if (getSubInstances() == null) {
            return Collections.emptyList();
        }

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
