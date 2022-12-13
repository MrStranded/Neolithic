package engine.data.entities;

import constants.ScriptConstants;
import engine.data.options.GameOptions;
import engine.data.variables.Variable;
import engine.graphics.gui.GuiData;
import engine.graphics.objects.GraphicalObject;
import engine.graphics.objects.gui.GuiObject;
import engine.graphics.objects.gui.Padding;
import engine.graphics.objects.gui.RenderSpace;
import engine.graphics.objects.gui.TemplateProcessor;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.math.numericalObjects.Matrix4;
import engine.parser.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GuiElement extends Instance {

    private List<GuiObject> guiObjects = new ArrayList<>(1);
    private boolean shouldUpdate = true;
    private boolean rendering = false;
    private RenderSpace renderSpace = null;

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
        if (shouldUpdate) {
            update();
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

    private void update() {
        layout(null);
        consolidate();
    }

    private void layout(RenderSpace parentSpace) {
        // determine available space
        renderSpace = new RenderSpace(this, parentSpace);

        // create gui objects
        setGuiObjects(TemplateProcessor.create(this, renderSpace));

        // layout subs
        for (GuiElement sub : getSubElements()) {
            sub.layout(renderSpace);
        }

        if (parentSpace != null) { parentSpace.useSpace(renderSpace); }
    }

    public void consolidate() {
        guiObjects.forEach(object -> {
            object.consolidateSize(renderSpace);
        });

        for (GuiElement sub : getSubElements()) {
            sub.consolidate();
        }

        shouldUpdate = false;
    }

    // ###################################################################################
    // ################################ Layouting ########################################
    // ###################################################################################

    public Optional<RenderSpace> getRenderSpace() {
        return Optional.ofNullable(renderSpace);
    }

    public void setGuiParent(GuiElement parent) {
        placeInto(parent);
    }

    public double[] getVariableAsDoubleArray(String variableName, int size) {
        double[] array = new double[size];

        List<Variable> list = getVariableSafe(variableName)
                .map(Variable::getList)
                .orElse(Collections.emptyList());
        int listSize = list.size();

        for (int i = 0; i < size && i < listSize; i++) {
            array[i] = list.get(i).getDouble();
        }

        return array;
    }

    public int getDepth() {
        GuiElement parent = (GuiElement) getSuperInstance();
        if (parent == null) { return 0; }

        return parent.getDepth() + 1;
    }

    public double getAbsWidth() {
        return guiObjects.stream()
                .filter(GuiObject::influencesSizeCalculations)
                .max((a, b) -> (int) Math.signum(a.getAbsWidth() - b.getAbsWidth()))
                .map(GuiObject::getAbsWidth)
                .orElse(0d);
    }

    public double getAbsHeight() {
        return guiObjects.stream()
                .filter(GuiObject::influencesSizeCalculations)
                .max((a, b) -> (int) Math.signum(a.getAbsHeight() - b.getAbsHeight()))
                .map(GuiObject::getAbsHeight)
                .orElse(0d);
    }

    public double getWidth() {
        double maxWidth = getAbsWidth();

        for (GuiObject object : guiObjects) {
            double width = object.getAbsWidth();
            if (width > maxWidth) { maxWidth = width; }
        }
        for (GuiElement sub : getSubElements()) {
            double width = sub.getWidth();
            if (width > maxWidth) { maxWidth = width; }
        }

        double[] padding = getVariableAsDoubleArray(ScriptConstants.GUI_PADDING, 4);

        return maxWidth + padding[Padding.LEFT] + padding[Padding.RIGHT];
    }

    public double getHeight() {
        return getPositionOfSubElement(null);
    }

    public double getPositionOfSubElement(GuiElement element) {
        double height = getAbsHeight();

        for (GuiElement sub : getSubElements()) {
            if (sub == element) {
                break;
            }

            height += sub.getPositionOfSubElement(element);
        }

        double[] padding = getVariableAsDoubleArray(ScriptConstants.GUI_PADDING, 4);

        return height + padding[Padding.TOP] + padding[Padding.BOTTOM];
    }

    public double getChildOffsetX() {
        double offset = 0;
        double parentWidth = GuiData.getRenderWindow().getWidth();

        Instance parent = getSuperInstance();
        if (parent instanceof GuiElement) {
            GuiElement p = ((GuiElement) parent);

            offset += p.getChildOffsetX();
            parentWidth = p.getWidth();
        }

        offset += parentWidth * getVariableSafe(ScriptConstants.GUI_RELATIVE_X).map(Variable::getDouble).orElse(0d);

        offset += getVariableSafe(ScriptConstants.GUI_ABSOLUTE_X).map(Variable::getDouble).orElse(0d);

        Logger.trace(toString() + " parent offset X: " + offset);

        return offset;
    }

    public double getChildOffsetY() {
        double offset = 0;
        double parentHeight = GuiData.getRenderWindow().getHeight();

        Instance parent = getSuperInstance();
        if (parent instanceof GuiElement) {
            GuiElement p = ((GuiElement) parent);

            offset += p.getChildOffsetY();
            parentHeight = p.getHeight();
        }

        offset += parentHeight * getVariableSafe(ScriptConstants.GUI_RELATIVE_Y).map(Variable::getDouble).orElse(0d);

        offset += getVariableSafe(ScriptConstants.GUI_ABSOLUTE_Y).map(Variable::getDouble).orElse(0d);

        Logger.trace(toString() + " parent offset Y: " + offset);

        return offset;
    }

    // ###################################################################################
    // ################################ Mouse ############################################
    // ###################################################################################

    public GuiElement getElementUnderMouse(double mouseX, double mouseY) {
        double width = getWidth();
        double height = getHeight();

        if (guiObjects.stream()
                .noneMatch(object -> object.isUnderMouse(mouseX, mouseY, width, height))
        ) {
            return null;
        }

        for (GuiElement element : getSubElements()) {
            GuiElement underMouse = element.getElementUnderMouse(mouseX, mouseY);
            if (underMouse != null) {
                return underMouse;
            }
        }

        return this;
    }

    // ###################################################################################
    // ################################ Clean Up #########################################
    // ###################################################################################

    @Override
    public void recursiveSlatingForRemoval() {
        super.recursiveSlatingForRemoval();
        GuiData.rememberToCleanGuiElement(this);
    }

    public void clearGuiObjects() {
        guiObjects.forEach(GraphicalObject::cleanUp);
        guiObjects.clear();
    }

    // ###################################################################################
    // ################################ Getters and Setters ##############################
    // ###################################################################################

    @Override
    public void addVariable(Variable variable) {
        super.addVariable(variable);
        shouldUpdate = true;
    }

    public void setGuiObjects(List<GuiObject> newObjects) {
        clearGuiObjects();
        guiObjects.addAll(newObjects);
    }

    public List<GuiObject> getGuiObjects() {
        return guiObjects;
    }

    /**
     * This will trigger an update of the guiObject of this element and all its children on the next render pass.
     */
    public void shouldUpdate() {
        shouldUpdate = true;
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

    public void debug(String prefix) {
        Logger.debug(prefix + toString());

        guiObjects.forEach(object -> object.debug(prefix));

        getSubElements().forEach(element -> element.debug(prefix + "    "));
    }

    public String toString() {
        return "Gui Element (id = " + id + (getName() != null ? " ,name = " + getName() : "") + ")";
    }
}
