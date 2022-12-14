package engine.graphics.objects.gui;

import constants.ScriptConstants;
import engine.data.entities.GuiElement;
import engine.graphics.gui.GuiData;
import engine.graphics.gui.GuiTemplate;
import engine.math.numericalObjects.Vector2;
import engine.parser.utils.Logger;

public class RenderSpace {

    private final GuiTemplate template;

    private Vector2 availableSpace;
    private double maxWidth, maxHeight;
    private boolean widthIsBounded, heightIsBounded;

    private Spacing margin;
    private Spacing padding;

    private Vector2 usedSpace = new Vector2(0,0);

    private Vector2 positionOnScreen = new Vector2(0, 0);

    public RenderSpace(GuiElement element, RenderSpace parentSpace) {
        template = GuiTemplate.from(element);

        element.getVariableSafe(ScriptConstants.GUI_MAX_WIDTH_ABS).ifPresent(value -> {
            maxWidth = value.getDouble();
            widthIsBounded = true; });
        element.getVariableSafe(ScriptConstants.GUI_MAX_HEIGHT_ABS).ifPresent(value -> {
            maxHeight = value.getDouble();
            heightIsBounded = true; });

        margin = new Spacing(element.getVariableAsDoubleArray(ScriptConstants.GUI_MARGIN, 4));
        padding = new Spacing(element.getVariableAsDoubleArray(ScriptConstants.GUI_PADDING, 4));

        if (parentSpace != null) {
            availableSpace = parentSpace.getMaxAvailableSpace();

            positionOnScreen = parentSpace.getNextChildPosition();
        } else {
            availableSpace = new Vector2(GuiData.getRenderWindow().getWidth(), GuiData.getRenderWindow().getHeight());
        }

        element.getVariableSafe(ScriptConstants.GUI_LEFT)
                .ifPresent(value -> positionOnScreen.setX(positionOnScreen.getX() + value.getDouble()));
        element.getVariableSafe(ScriptConstants.GUI_TOP)
                .ifPresent(value -> positionOnScreen.setY(positionOnScreen.getY() + value.getDouble()));
    }

    public void placeObject(GuiObject object) {
        Vector2 position;

        if (object.influencesSizeCalculations()) {
            position = getNextChildPosition();

            useSpace(object);
        } else {
            position = getBoxOrigin();
        }

        object.setAbsoluteOffset(position.getX(), position.getY());
    }

    private void useSpace(GuiObject object) {
        Logger.debug("Use space for object: " + object);

        useSpace(object.getAbsWidth(), object.getAbsHeight());
    }

    public void useSpace(RenderSpace sub) {
        Logger.debug("Use space for renderSpace: " + sub);

        Vector2 fullSize = sub.getFullSize();

        useSpace(fullSize.getX(), fullSize.getY());
    }

    private void useSpace(double width, double height) {
        switch (template) {
            case HORIZONTAL -> {
                usedSpace.setX(usedSpace.getX() + width);
                usedSpace.setY(Math.max(usedSpace.getY(), height));
            }
            case VERTICAL -> {
                usedSpace.setX(Math.max(usedSpace.getX(), width));
                usedSpace.setY(usedSpace.getY() + height);
            }
        }
    }

    public Vector2 getFullSize() {
        Vector2 fullSize = getBoxSize();

        fullSize.setX(fullSize.getX() + margin.getLeft() + margin.getRight());
        fullSize.setY(fullSize.getY() + margin.getTop() + margin.getBottom());

        return fullSize;
    }

    public Vector2 getBoxSize() {
        Vector2 boxSize = getContentSize();

        boxSize.setX(boxSize.getX() + padding.getLeft() + padding.getRight());
        boxSize.setY(boxSize.getY() + padding.getTop() + padding.getBottom());

        return boxSize;
    }

    public Vector2 getContentSize() {
        return new Vector2(usedSpace);
    }

    public Vector2 getMaxAvailableSpace() {
        Vector2 maxAvailableSpace = new Vector2(availableSpace);

        if (widthIsBounded) { maxAvailableSpace.setX(Math.min(maxAvailableSpace.getX(), maxWidth)); }
        if (heightIsBounded) { maxAvailableSpace.setY(Math.min(maxAvailableSpace.getY(), maxHeight)); }

        return maxAvailableSpace;
    }

    private Vector2 getNextChildPosition() {
        Vector2 childPosition = getBoxOrigin();

        childPosition.setX(childPosition.getX() + padding.getLeft());
        childPosition.setY(childPosition.getY() + padding.getTop());

        switch (template) {
            case HORIZONTAL:
                childPosition.setX(childPosition.getX() + usedSpace.getX());
                break;
            case VERTICAL:
                childPosition.setY(childPosition.getY() + usedSpace.getY());
                break;
        }

        return childPosition;
    }

    private Vector2 getBoxOrigin() {
        double x = positionOnScreen.getX() + margin.getLeft();
        double y = positionOnScreen.getY() + margin.getTop();

        return new Vector2(x, y);
    }

    public String toString() {
        return String.format("(position = %s, margin = %s, padding = %s, usedSpace = %s)", positionOnScreen, margin, padding, usedSpace);
    }
}
