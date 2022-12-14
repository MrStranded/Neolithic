package engine.graphics.objects.gui;

import engine.graphics.gui.GuiData;
import engine.graphics.objects.generators.MeshGenerator;
import engine.graphics.renderer.color.RGBA;
import engine.parser.utils.Logger;

import java.util.function.BiConsumer;

public class GuiBuilder<T extends GuiObject> {

    private final T object;

    private GuiBuilder(T object) {
        this.object = object;
    }

    // ###################################################################################
    // ################################ TextObject #######################################
    // ###################################################################################

    public static GuiBuilder<TextObject> Text(String text) {
        return new GuiBuilder<>(
                new TextObject(text, GuiData.getFontTexture())
        );
    }
    public static GuiBuilder<TextObject> Text(String text, RGBA color, double maxAbsTextObjectWidth) {
        return new GuiBuilder<>(
                new TextObject(text, GuiData.getFontTexture(), color, maxAbsTextObjectWidth)
        );
    }

    public GuiBuilder<T> withTextSize(double textSize) {
        if (object instanceof TextObject) {
            TextObject textObject = (TextObject) object;
            object.setSizeOnScreen(textObject.getTextWidth() * textSize, textObject.getTextHeight() * textSize);

        } else {
            Logger.error("GuiObject was not of type TextObject: " + object);
        }
        return this;
    }

    // ###################################################################################
    // ################################ Sprite ###########################################
    // ###################################################################################

    public static GuiBuilder<GuiObject> Sprite() {
        return new GuiBuilder<>(
                new GuiObject(MeshGenerator.createQuad())
        );
    }

    // ###################################################################################
    // ################################ General ##########################################
    // ###################################################################################

    public GuiBuilder<T> withTexture(String texturePath) {
        object.setTexture(texturePath);
        return this;
    }

    public GuiBuilder<T> withSize(double width, double height) {
        object.setSizeOnScreen(width, height);
        return this;
    }

    public GuiBuilder<T> withResize(BiConsumer<GuiObject, RenderSpace> callback) {
        object.setResizeCallback(callback);
        return this;
    }

    public GuiBuilder<T> influenceSizeCalculations(boolean influence) {
        object.setInfluenceSizeCalculations(influence);
        return this;
    }

    public GuiBuilder<T> withZIndex(double zIndex) {
        object.move(0, 0, zIndex);
        return this;
    }

    // ###################################################################################
    // ################################ Build ############################################
    // ###################################################################################

    public T build() {
        return object;
    }
}
