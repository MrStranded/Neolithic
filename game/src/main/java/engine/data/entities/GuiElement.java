package engine.data.entities;

import constants.ScriptConstants;
import engine.data.options.GameOptions;
import engine.data.variables.Variable;
import engine.graphics.gui.GuiData;
import engine.graphics.objects.GraphicalObject;
import engine.graphics.objects.gui.GuiObject;
import engine.graphics.objects.gui.GuiObjectRole;
import engine.graphics.objects.gui.RenderSpace;
import engine.graphics.objects.gui.TemplateProcessor;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.input.MouseInput;
import engine.math.numericalObjects.Matrix4;
import engine.parser.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GuiElement extends Instance {

    private List<GuiObject> guiObjects = new ArrayList<>(1);
    private boolean shouldUpdate = false;
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

    public void render(ShaderProgram hudShaderProgram, Matrix4 orthographicMatrix, MouseInput mouse) {
        if (isSlatedForRemoval() || GameOptions.reloadScripts) {
            return;
        }

        rendering = true;

        // there were updates
        if (shouldUpdate) {
            update(mouse);
        }

        // render self
        guiObjects.stream()
                .filter(GuiObject::isVisible)
                .forEach(object -> {
                    hudShaderProgram.setUniform("projectionViewMatrix", orthographicMatrix.times(object.getWorldMatrix()));
                    object.renderForGUI();
                });

        // render subs
        getSubElements().forEach(element -> element.render(hudShaderProgram, orthographicMatrix, mouse));

        rendering = false;
    }

    // ###################################################################################
    // ################################ Create Gui #######################################
    // ###################################################################################

    private void update(MouseInput mouse) {
        if (getSuperInstance() != null) {
            ((GuiElement) getSuperInstance()).update(mouse);
        } else {
            createGuiObjects(null);

            updateBackgrounds(mouse);
        }
    }

    private void createGuiObjects(RenderSpace parentSpace) {
        // determine available space
        renderSpace = new RenderSpace(this, parentSpace);

        // create gui objects
        setGuiObjects(TemplateProcessor.create(this, renderSpace));

        // layout subs
        for (GuiElement sub : getSubElements()) {
            sub.createGuiObjects(renderSpace);
        }

        if (parentSpace != null) { parentSpace.useSpace(renderSpace); }

        consolidate();

        shouldUpdate = false;
    }

    public void consolidate() {
        guiObjects.forEach(object -> {
            object.recalculate(renderSpace);
        });

        getSubElements().forEach(GuiElement::consolidate);
    }

    // ###################################################################################
    // ################################ Layouting ########################################
    // ###################################################################################

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

    // ###################################################################################
    // ################################ Mouse ############################################
    // ###################################################################################

    public GuiElement getElementUnderMouse(double mouseX, double mouseY) {
        for (GuiElement element : getSubElements()) {
            GuiElement underMouse = element.getElementUnderMouse(mouseX, mouseY);
            if (underMouse != null) {
                return underMouse;
            }
        }

        if (guiObjects.stream().anyMatch(object -> object.isUnderMouse(mouseX, mouseY))) {
            return this;
        }

        return null;
    }

    public void updateBackgrounds(MouseInput mouse) {
        getSubElements().forEach(sub -> sub.updateBackgrounds(mouse));

        guiObjects.stream()
                .filter(object -> object.getRole().isBackground())
                .forEach(object -> object.setVisible(false));

        if (mouse.isLeftButtonPressed()) {
            Optional<GuiObject> backgroundPressed = getObject(GuiObjectRole.BACKGROUND_PRESSED);
            if (backgroundPressed.isPresent() && backgroundPressed.get().isUnderMouse(mouse.getXPos(), mouse.getYPos())) {
                backgroundPressed.get().setVisible(true);
                return;
            }
        }

        Optional<GuiObject> backgroundHover = getObject(GuiObjectRole.BACKGROUND_HOVER);
        if (backgroundHover.isPresent() && backgroundHover.get().isUnderMouse(mouse.getXPos(), mouse.getYPos())) {
            backgroundHover.get().setVisible(true);
            return;
        }

        getObject(GuiObjectRole.BACKGROUND)
                .ifPresent(background -> background.setVisible(true));
    }

    private Optional<GuiObject> getObject(GuiObjectRole role) {
        return guiObjects.stream()
                .filter(object -> role == object.getRole())
                .findFirst();
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
        shouldUpdate();
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
    }

    public boolean isRendering() {
        return rendering;
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
