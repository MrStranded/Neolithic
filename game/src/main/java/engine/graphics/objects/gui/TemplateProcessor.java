package engine.graphics.objects.gui;

import constants.GraphicalConstants;
import constants.ScriptConstants;
import engine.data.entities.GuiElement;
import engine.data.variables.Variable;
import engine.graphics.gui.GuiData;
import engine.graphics.gui.GuiTemplates;
import engine.graphics.renderer.color.RGBA;
import engine.parser.utils.Logger;
import org.lwjglx.debug.joptsimple.internal.Strings;

public class TemplateProcessor {

    private static final double Z_STEP = 1d / 256d;

    public static GuiObject[] create(GuiElement element, GuiTemplates template) {
        Logger.trace("Creating gui object " + element.getName() + " for template '" + template + "'");

        int depth = element.getDepth() + 1;

        switch (template) {
            case TEXT:
                String text = element.getVariableSafe(ScriptConstants.GUI_TEXT).map(Variable::getString).orElse("");
                double xRelPos = element.getVariableSafe(ScriptConstants.GUI_RELATIVE_X).map(Variable::getDouble).orElse(0d);
                double yRelPos = element.getVariableSafe(ScriptConstants.GUI_RELATIVE_Y).map(Variable::getDouble).orElse(0d);
                double textSize = element.getVariableSafe(ScriptConstants.GUI_TEXT_SIZE).map(Variable::getDouble).orElse(GraphicalConstants.DEFAULT_FONT_SIZE);
                RGBA color = element.getVariableSafe(ScriptConstants.GUI_TEXT_COLOR).map(Variable::getRGBA).orElse(RGBA.WHITE);
                String backgroundPath = element.getVariableSafe(ScriptConstants.GUI_BACKGROUND).map(Variable::getString).orElse("");

                double maxAbsTextObjectWidth = (double) element.getAbsoluteMaxSelfWidth() / textSize * (double) GuiData.getFontTexture().getHeight();

                TextObject textObject = (TextObject) GuiBuilder.Text(text, color, maxAbsTextObjectWidth)
                        .withSize(textSize)
                        .withPosition(xRelPos, yRelPos)
                        .withZIndex(-1d + depth * Z_STEP)
                        .build();

                GuiObject background = null;
                if (! Strings.isNullOrEmpty(backgroundPath)) {
                    background = GuiBuilder.Sprite()
                            .withTexture(backgroundPath)
                            .withPosition(xRelPos, yRelPos)
                            .withResize((o, e) -> o.setAbsoluteSize(e.getWidth(), e.getHeight()))
                            .influenceSizeCalculations(false)
                            .withZIndex(-1d + (depth - 0.5d) * Z_STEP)
                            .build();
                }

                if (background != null) {
                    return new GuiObject[] {textObject, background};
                } else {
                    return new GuiObject[] {textObject};
                }

            default:
                return new GuiObject[] {GuiBuilder.Text("Unrecognized template: " + template).build()};
        }
    }

}
