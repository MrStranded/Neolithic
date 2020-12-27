package engine.input;

import constants.GameConstants;
import constants.GraphicalConstants;
import engine.data.Data;
import engine.data.options.GameOptions;
import engine.data.planetary.Tile;
import engine.data.proto.Container;
import engine.data.scripts.ScriptRun;
import engine.data.variables.DataType;
import engine.data.variables.Variable;
import engine.graphics.gui.window.Window;
import engine.graphics.objects.Camera;
import engine.graphics.objects.Scene;
import engine.graphics.renderer.Renderer;
import engine.math.MousePicking;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

public class IngameInteractions {

    private Window window;
    private Renderer renderer;

    private MouseInput mouse;
    private KeyboardInput keyboard;

    public IngameInteractions(Window window, Renderer renderer) {
        this.window = window;
        this.renderer = renderer;

        mouse = new MouseInput(window);
        keyboard = new KeyboardInput(window);
    }

    public void processInput(Scene scene) {
        Camera camera = scene.getCamera();
        double dist = (camera.getRadius() - 0.5d) / 100d;

        if (keyboard.isPressed(GLFW.GLFW_KEY_A)) { // rotate left
            camera.rotateYaw(-dist);
        }
        if (keyboard.isPressed(GLFW.GLFW_KEY_D)) { // rotate right
            camera.rotateYaw(dist);
        }
        if (keyboard.isPressed(GLFW.GLFW_KEY_E)) { // look down
            camera.rotateTilt(-0.005d);
        }
        if (keyboard.isPressed(GLFW.GLFW_KEY_Q)) { // look up
            camera.rotateTilt(0.005d);
        }
        if (keyboard.isPressed(GLFW.GLFW_KEY_W)) { // rotate up
            camera.rotatePitch(-dist);
        }
        if (keyboard.isPressed(GLFW.GLFW_KEY_S)) { // rotate down
            camera.rotatePitch(dist);
        }
        if (keyboard.isPressed(GLFW.GLFW_KEY_R) && ! keyboard.ctrlPressed()) { // go closer
            camera.changeRadius(-dist);
        }
        if (keyboard.isPressed(GLFW.GLFW_KEY_F)) { // go farther away
            camera.changeRadius(dist);
        }
        if (camera.getRadius() < 1d + GraphicalConstants.ZNEAR) { // ensure not to go too close
            camera.setRadius(1d + GraphicalConstants.ZNEAR);
        }

        if (keyboard.isClicked(GLFW.GLFW_KEY_G)) {
            Data.addScriptRun(new ScriptRun(Data.getMainInstance(), "repopulate", null));
        }
        if (keyboard.isClicked(GLFW.GLFW_KEY_T)) {
            Data.addScriptRun(new ScriptRun(Data.getMainInstance(), "armageddon", null));
        }
        if (keyboard.isClicked(GLFW.GLFW_KEY_O)) {
            Data.addScriptRun(new ScriptRun(Data.getMainInstance(), "fit", null));
        }

        if (keyboard.ctrlPressed(GLFW.GLFW_KEY_R)) {
            GameOptions.reloadScripts = true;
        }
        if (keyboard.isClicked(GLFW.GLFW_KEY_X)) {
            GameOptions.printPerformance = !GameOptions.printPerformance;
        }
        if (keyboard.isClicked(GLFW.GLFW_KEY_P)) {
            GameOptions.plotEntities = !GameOptions.plotEntities;
        }
        if (keyboard.isClicked(GLFW.GLFW_KEY_SPACE)) {
            GameOptions.runTicks = !GameOptions.runTicks;
        }
        if (keyboard.isClicked(GLFW.GLFW_KEY_PERIOD)) {
            GameOptions.runTicks = true;
            GameOptions.stopAtNextTick = true;
        }

        if (keyboard.isClicked(GLFW.GLFW_KEY_UP)) {
            nextType(1);
        }
        if (keyboard.isClicked(GLFW.GLFW_KEY_DOWN)) {
            nextType(-1);
        }
        if (mouse.getZSpeed() > 0) {
            for (int i = 0; i < mouse.getZSpeed(); i++) {
                nextType(1);
            }
        }
        if (mouse.getZSpeed() < 0) {
            for (int i = 0; i < -mouse.getZSpeed(); i++) {
                nextType(-1);
            }
        }

        if (mouse.isLeftButtonClicked()) {
            Tile clickedTile = MousePicking.getClickedTile(mouse.getXPos(), mouse.getYPos(), renderer, scene);
            if (clickedTile != null) {
                //scene.setFacePartOverlay(clickedTile.getTileMesh());
                Data.addScriptRun(new ScriptRun(
                        Data.getMainInstance(),
                        "leftClick",
                        new Variable[] {
                                new Variable(clickedTile),
                                new Variable(Data.getContainer(GameOptions.currentContainerId).orElse(null))
                        }));
            }
        }
        if (mouse.isRightButtonClicked()) {
            Tile clickedTile = MousePicking.getClickedTile(mouse.getXPos(), mouse.getYPos(), renderer, scene);
            if (clickedTile != null) {
//                if (clickedTile.getSubInstances() == null) {
                    GameOptions.selectedInstance = clickedTile;
//                } else {
//                    for (Instance sub : clickedTile.getSubInstances()) {
//                        GameOptions.selectedInstance = sub;
//                        break;
//                    }
//                }
            }
			/*if (clickedTile != null) {
				//scene.setFacePartOverlay(clickedTile.getTileMesh());
				Data.addScriptRun(new ScriptRun(
						Data.getMainInstance(),
						"rightClick",
						new Variable[]{
								new Variable(clickedTile),
								new Variable(Data.getContainer(GameOptions.currentContainerId))}));
			}*/
        }

        // closing window
        if (keyboard.isClicked(GLFW.GLFW_KEY_ESCAPE)) {
            //cleanUp(); // somehow enabling this here causes the program to not close properly anymore
            window.close();
        }

        mouse.flush();
    }

    private void nextType(int direction) {
        int id = GameOptions.currentContainerId + direction;
        while (true) {
            if (id < 0) {
                id += GameConstants.MAX_CONTAINERS;
            } else if (id >= GameConstants.MAX_CONTAINERS) {
                id -= GameConstants.MAX_CONTAINERS;
            }

            Optional<Container> container = Data.getContainer(id);
            if (container.isPresent()) {
                if (container.get().getType() == DataType.CREATURE
                        || container.get().getType() == DataType.FORMATION
                        || container.get().getType() == DataType.TILE
                        || container.get().getType() == DataType.ENTITY) {
                    GameOptions.currentContainerId = id;
                    break;
                }
            }
            id += direction;
        }
    }

}
