package engine.graphics.objects.gui;

import constants.ScriptConstants;
import engine.data.entities.GuiElement;
import engine.graphics.gui.GuiData;
import engine.graphics.gui.GuiTemplate;
import engine.parser.utils.Logger;

public class RenderSpace {

    private final GuiTemplate template;

    private double availableWidth, availableHeight;
    private double maxWidth, maxHeight;
    private boolean widthIsBounded, heightIsBounded;

    private double[] padding = {0, 0, 0, 0};
    private double[] margin = {0, 0, 0, 0};

    // how much space will be needed?
    private double usedSpaceX = 0;
    private double usedSpaceY = 0;

    // where are we in the process of positioning?
    private double positionX = 0;
    private double positionY = 0;

    public RenderSpace(GuiElement element, RenderSpace parentSpace) {
        template = GuiTemplate.from(element);

        element.getVariableSafe(ScriptConstants.GUI_LEFT).ifPresent(value -> positionX = value.getDouble());
        element.getVariableSafe(ScriptConstants.GUI_TOP).ifPresent(value -> positionY = value.getDouble());

        element.getVariableSafe(ScriptConstants.GUI_MAX_WIDTH_ABS).ifPresent(value -> {
            maxWidth = value.getDouble();
            widthIsBounded = true; });
        element.getVariableSafe(ScriptConstants.GUI_MAX_HEIGHT_ABS).ifPresent(value -> {
            maxHeight = value.getDouble();
            heightIsBounded = true; });

        padding = element.getVariableAsDoubleArray(ScriptConstants.GUI_PADDING, 4);
        margin = element.getVariableAsDoubleArray(ScriptConstants.GUI_MARGIN, 4);

        if (parentSpace != null) {
            availableWidth = parentSpace.getMaxChildWidth();
            availableHeight = parentSpace.getMaxChildHeight();

            positionX = parentSpace.getChildPositionX();
            positionY = parentSpace.getChildPositionY();
        } else {
            availableWidth = GuiData.getRenderWindow().getWidth();
            availableHeight = GuiData.getRenderWindow().getHeight();
        }
    }

    public void useSpace(GuiObject object) {
        Logger.debug("Use space for object: " + object);

        switch (template) {
            case HORIZONTAL -> {
                usedSpaceX += object.getAbsWidth();
                usedSpaceY = Math.max(usedSpaceY, object.getAbsHeight());
            }
            case VERTICAL -> {
                usedSpaceX = Math.max(usedSpaceX, object.getAbsWidth());
                usedSpaceY += object.getAbsHeight();
            }
        }
    }

    public void placeObject(GuiObject object) {
        double x = positionX + margin[Padding.LEFT];
        double y = positionY + margin[Padding.TOP];

        if (object.influencesSizeCalculations()) {
            x += padding[Padding.LEFT];
            y += padding[Padding.TOP];

            switch (template) {
                case HORIZONTAL -> x += usedSpaceX;
                case VERTICAL -> y += usedSpaceY;
            }
        }

        object.setAbsoluteOffset(x, y);
    }

    public void useSpace(RenderSpace sub) {
        Logger.debug("Use space for renderSpace: " + sub);

        switch (template) {
            case HORIZONTAL -> {
                usedSpaceX += sub.getFullUsedWidth();
                usedSpaceY = Math.max(usedSpaceY, sub.getFullUsedHeight());
            }
            case VERTICAL -> {
                usedSpaceX = Math.max(usedSpaceX, sub.getFullUsedWidth());
                usedSpaceY += sub.getFullUsedHeight();
            }
        }
    }

    public double getMaxChildWidth() {
        double width = availableWidth - padding[Padding.LEFT] - padding[Padding.RIGHT];

        if (widthIsBounded) { width = Math.min(width, maxWidth); }

        return width;
    }

    public double getFullUsedWidth() {
        return usedSpaceX + padding[Padding.LEFT] + padding[Padding.RIGHT];
    }

    public double getMaxChildHeight() {
        double height = availableHeight - padding[Padding.TOP] - padding[Padding.BOTTOM];

        if (heightIsBounded) { height = Math.min(height, maxHeight); }

        return height;
    }

    public double getFullUsedHeight() {
        return usedSpaceY + padding[Padding.TOP] + padding[Padding.BOTTOM];
    }

    private double getChildPositionX() {
        double x = positionX + padding[Padding.LEFT] + margin[Padding.LEFT];

        if (template == GuiTemplate.HORIZONTAL) {
            x += usedSpaceX;
        }

        return x;
    }

    private double getChildPositionY() {
        double y = positionY + padding[Padding.TOP] + margin[Padding.TOP];

        if (template == GuiTemplate.VERTICAL) {
            y += usedSpaceY;
        }

        return y;
    }

    public String toString() {
        return String.format("(usedPositionX = %s, usedPositionY = %s, usedSpaceX = %s, usedSpaceY = %s)", positionX, positionY, usedSpaceX, usedSpaceY);
    }

}
