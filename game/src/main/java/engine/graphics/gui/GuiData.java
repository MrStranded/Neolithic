package engine.graphics.gui;

import engine.graphics.gui.statistics.StatisticsWindow;
import engine.graphics.gui.window.Window;
import engine.graphics.objects.Scene;
import engine.graphics.renderer.Renderer;

import java.awt.*;

public class GuiData {

    private static Dimension screenSize;

    private static Renderer renderer;
    private static Window renderWindow;
    private static StatisticsWindow statisticsWindow;

    private static Scene scene;
    private static GUIInterface hud;

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
    }

    // ###################################################################################
    // ################################ Clean up #########################################
    // ###################################################################################

    public static void clear() {
        if (renderer != null) { renderer.cleanUp(); }
        if (scene != null) { scene.cleanUp(); }
        if (hud != null) { hud.cleanUp(); }

        if (renderWindow != null) { renderWindow.destroy(); }
        if (statisticsWindow != null) { statisticsWindow.close(); }
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
}
