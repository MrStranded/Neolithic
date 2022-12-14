package engine.graphics.objects.gui;

import constants.GraphicalConstants;
import constants.ScriptConstants;
import engine.data.entities.GuiElement;
import engine.data.variables.Variable;
import engine.graphics.gui.GuiData;
import engine.graphics.renderer.color.RGBA;
import engine.math.numericalObjects.Vector2;
import engine.parser.utils.Logger;
import org.lwjglx.debug.joptsimple.internal.Strings;

import java.util.ArrayList;
import java.util.List;

public class TemplateProcessor {

    private static final double Z_STEP = 1d / 256d;

    private TemplateProcessor() {}

    public static List<GuiObject> create(GuiElement element, RenderSpace renderSpace) {
        Logger.trace("Creating gui object " + element.getName());

        List<GuiObject> objects = new ArrayList<>();
        int depth = element.getDepth() + 1;

        String text = element.getVariableSafe(ScriptConstants.GUI_TEXT)
                .map(Variable::getString).orElse("");
        double textSize = element.getVariableSafe(ScriptConstants.GUI_TEXT_SIZE)
                .map(Variable::getDouble).orElse(GraphicalConstants.DEFAULT_FONT_SIZE);
        RGBA textColor = element.getVariableSafe(ScriptConstants.GUI_TEXT_COLOR)
                .map(Variable::getRGBA).orElse(RGBA.WHITE);
        String backgroundPath = element.getVariableSafe(ScriptConstants.GUI_BACKGROUND)
                .map(Variable::getString).orElse("");
        String backgroundHoverPath = element.getVariableSafe(ScriptConstants.GUI_BACKGROUND_HOVER)
                .map(Variable::getString).orElse("");
        String backgroundPressedPath = element.getVariableSafe(ScriptConstants.GUI_BACKGROUND_PRESSED)
                .map(Variable::getString).orElse("");

        double maxAbsTextObjectWidth = textSize != 0
                ? renderSpace.getMaxAvailableSpace().getX() * (double) GuiData.getFontTexture().getHeight() / textSize
                : 0;

        if (! Strings.isNullOrEmpty(text)) {
            TextObject textObject = createTextObject(depth, text, textSize, textColor, maxAbsTextObjectWidth);
            renderSpace.placeObject(textObject);
            objects.add(textObject);
        }

        if (! Strings.isNullOrEmpty(backgroundPath)) {
            GuiObject background = createBackgroundObject(depth, backgroundPath, GuiObjectRole.BACKGROUND);
            renderSpace.placeObject(background);
            objects.add(background);
        }

        if (! Strings.isNullOrEmpty(backgroundHoverPath)) {
            GuiObject backgroundHover = createBackgroundObject(depth, backgroundHoverPath, GuiObjectRole.BACKGROUND_HOVER);
            renderSpace.placeObject(backgroundHover);
            objects.add(backgroundHover);
        }

        if (! Strings.isNullOrEmpty(backgroundPressedPath)) {
            GuiObject backgroundPressed = createBackgroundObject(depth, backgroundPressedPath, GuiObjectRole.BACKGROUND_PRESSED);
            renderSpace.placeObject(backgroundPressed);
            objects.add(backgroundPressed);
        }

        return objects;
    }

    private static TextObject createTextObject(int depth, String text, double textSize, RGBA textColor, double maxAbsTextObjectWidth) {
        return GuiBuilder.Text(text, textColor, maxAbsTextObjectWidth)
                .withTextSize(textSize)
                .withZIndex(-1d + depth * Z_STEP)
                .build();
    }

    private static GuiObject createBackgroundObject(int depth, String backgroundPath, GuiObjectRole role) {
        return GuiBuilder.Sprite()
                        .withTexture(backgroundPath)
                        .withResize((object, rs) -> {
                            Vector2 boxSize = rs.getBoxSize();
                            object.setSizeOnScreen(boxSize.getX(), boxSize.getY());
                        })
                        .withRole(role)
                        .withZIndex(-1d + (depth - 0.5d) * Z_STEP)
                        .build();
    }

}
