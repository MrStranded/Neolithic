package engine.graphics.objects.gui;

import constants.GraphicalConstants;
import constants.ScriptConstants;
import engine.data.entities.GuiElement;
import engine.data.variables.Variable;
import engine.graphics.gui.GuiData;
import engine.graphics.renderer.color.RGBA;
import engine.parser.utils.Logger;
import org.lwjglx.debug.joptsimple.internal.Strings;

import java.util.ArrayList;
import java.util.List;

public class TemplateProcessor {

    private static final double Z_STEP = 1d / 256d;

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

        double maxAbsTextObjectWidth = renderSpace.getMaxChildWidth() * (double) GuiData.getFontTexture().getHeight() / textSize;

        if (! Strings.isNullOrEmpty(text)) {
            TextObject textObject = GuiBuilder.Text(text, textColor, maxAbsTextObjectWidth)
                    .withSize(textSize)
                    .withZIndex(-1d + depth * Z_STEP)
                    .build();

            renderSpace.placeObject(textObject);
            renderSpace.useSpace(textObject);

            objects.add(textObject);
        }

        if (! Strings.isNullOrEmpty(backgroundPath)) {
            GuiObject background = GuiBuilder.Sprite()
                    .withTexture(backgroundPath)
                    .withResize((object, rs) -> {
                        object.setAbsoluteSize(
                                rs.getFullUsedWidth(),
                                rs.getFullUsedHeight()
                        );
                        Logger.debug("Resized background " + object);
                    })
                    .influenceSizeCalculations(false)
                    .withZIndex(-1d + (depth - 0.5d) * Z_STEP)
                    .build();

            renderSpace.placeObject(background);

            objects.add(background);
        }

        return objects;
    }

}
