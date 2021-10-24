package engine.graphics.objects.gui;

import engine.data.entities.GuiElement;
import engine.graphics.gui.GuiData;
import engine.graphics.objects.generators.MeshGenerator;
import engine.graphics.renderer.color.RGBA;
import engine.parser.utils.Logger;

import java.util.function.BiConsumer;

public class GuiBuilder {

    private final GuiObject object;

    private GuiBuilder(GuiObject object) {
        this.object = object;
    }

    // ###################################################################################
    // ################################ TextObject #######################################
    // ###################################################################################

    public static GuiBuilder Text(String text) {
        return new GuiBuilder(
                new TextObject(text, GuiData.getFontTexture())
        );
    }
    public static GuiBuilder Text(String text, RGBA color, double maxAbsTextObjectWidth) {
        return new GuiBuilder(
                new TextObject(text, GuiData.getFontTexture(), color, maxAbsTextObjectWidth)
        );
    }

    public GuiBuilder withSize(double size) {
        if (object instanceof TextObject) {
            TextObject textObject = (TextObject) object;
            textObject.setAbsoluteSize(textObject.getTextWidth() * size, textObject.getTextHeight() * size);

        } else {
            Logger.error("GuiObject was not of type TextObject: " + object);
        }
        return this;
    }

    // ###################################################################################
    // ################################ Sprite ###########################################
    // ###################################################################################

    public static GuiBuilder Sprite() {
        return new GuiBuilder(
                new GuiObject(MeshGenerator.createQuad())
        );
    }

    // ###################################################################################
    // ################################ General ##########################################
    // ###################################################################################

    public GuiBuilder withPosition(double xRelPos, double yRelPos) {
        object.setRelativeOffset(xRelPos, yRelPos);
        return this;
    }

    public GuiBuilder withTexture(String texturePath) {
        object.setTexture(texturePath);
        return this;
    }

    public GuiBuilder withSize(double width, double height) {
        object.setAbsoluteSize(width, height);
        return this;
    }

    public GuiBuilder withResize(BiConsumer<GuiObject, GuiElement> callback) {
        object.setResizeCallback(callback);
        return this;
    }

    public GuiBuilder influenceSizeCalculations(boolean influence) {
        object.setInfluenceSizeCalculations(influence);
        return this;
    }

    public GuiBuilder withZIndex(double zIndex) {
        object.move(0, 0, zIndex);
        return this;
    }

    // ###################################################################################
    // ################################ Build ############################################
    // ###################################################################################

    public GuiObject build() {
        return object;
    }
}
