package engine.graphics.gui;

import constants.GraphicalConstants;
import engine.data.entities.GuiElement;
import engine.graphics.gui.statistics.StatisticsWindow;
import engine.graphics.gui.window.Window;
import engine.graphics.objects.Scene;
import engine.graphics.objects.textures.FontTexture;
import engine.graphics.renderer.Renderer;
import engine.parser.utils.Logger;

import java.awt.*;
import java.io.InputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GuiData {

    private static Dimension screenSize;

    private static Renderer renderer;
    private static Window renderWindow;
    private static StatisticsWindow statisticsWindow;

    private static Scene scene;
    private static GUIInterface hud;
    private static FontTexture fontTexture;

    private static ConcurrentLinkedQueue<GuiElement> guiElementsToClean;

    // ###################################################################################
    // ################################ Initialization ###################################
    // ###################################################################################

    public static void initialize() {
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        renderWindow = new Window((int) screenSize.getWidth()/2,(int) screenSize.getHeight()*3/4,"Neolithic");

        renderer = new Renderer(renderWindow);

        statisticsWindow = new StatisticsWindow((int) screenSize.getWidth()/2,(int) screenSize.getHeight()*3/4, "Statistics");

        scene = new Scene();
        hud = new BaseGUI();

        guiElementsToClean = new ConcurrentLinkedQueue<>();

        initializeFonts();
    }

    private static void initializeFonts() {
        try {
            InputStream fontStream = FontTexture.class.getResourceAsStream(GraphicalConstants.DEFAULT_FONT);

            Font font;
            if (fontStream != null) {
                font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.PLAIN, GraphicalConstants.FONT_SIZE_FOR_TEXTURE);
            } else {
                Logger.error("Could not load custom font! Using fallback font instead.");
                font = new Font(GraphicalConstants.FALLBACK_FONT, Font.PLAIN, GraphicalConstants.FONT_SIZE_FOR_TEXTURE);
            }

            fontTexture = new FontTexture(font, "US-ASCII"); // UTF-8 , UTF-16 , US-ASCII , ISO-8859-1 (the utf charsets don't work)

        } catch (Exception e) {
            Logger.error("Font texture could not be created");
            e.printStackTrace();
        }
    }

    // ###################################################################################
    // ################################ Clean up #########################################
    // ###################################################################################

    public static void clear() {
        if (renderer != null) { renderer.cleanUp(); }
        if (scene != null) { scene.cleanUp(); }
        if (hud != null) { hud.clear(); }

        if (renderWindow != null) { renderWindow.destroy(); }
        if (statisticsWindow != null) { statisticsWindow.close(); }

        clearGuiElements();
    }

    public static void clearGuiElements() {
        GuiElement next;
        while ((next = guiElementsToClean.peek()) != null) {
            if (next.isRendering()) { return; }

            guiElementsToClean.poll().clearGuiObjects();
        }
    }
    public static void rememberToCleanGuiElement(GuiElement element) {
        guiElementsToClean.add(element);
    }

    // ###################################################################################
    // ################################ Getters and Setters ##############################
    // ###################################################################################

    public static Renderer getRenderer() {
        return renderer;
    }

    public static Window getRenderWindow() {
        return renderWindow;
    }

    public static StatisticsWindow getStatisticsWindow() {
        return statisticsWindow;
    }

    public static Scene getScene() {
        return scene;
    }

    public static GUIInterface getHud() {
        return hud;
    }

    public static FontTexture getFontTexture() {
        return fontTexture;
    }
}
