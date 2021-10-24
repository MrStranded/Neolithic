package engine.math;

import engine.data.Data;
import engine.data.entities.GuiElement;
import engine.data.entities.Tile;
import engine.graphics.gui.GuiData;
import engine.graphics.objects.Scene;
import engine.graphics.objects.planet.FacePart;
import engine.graphics.renderer.Renderer;
import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;
import engine.math.numericalObjects.Vector4;

public class MousePicking {

    public static Tile getClickedTile(double mouseX, double mouseY, Renderer renderer, Scene scene) {
        FacePart facePart = getClickedFacePart(mouseX, mouseY, renderer, scene);
        if (facePart != null && facePart.getTile() != null && facePart.getQuarterFaces() == null) {
            return facePart.getTile();
        }
        return null;
    }

    public static FacePart getClickedFacePart(double mouseX, double mouseY, Renderer renderer, Scene scene) {
        // calculate mouse position from screen coordinates, to "camera" coordinates
        // x: [0,width] -> [-1,1]
        // y: [0,height] -> [1,-1] : positive is up
        double positionX = 2d * mouseX / (double) renderer.getWindow().getWidth() - 1d;
        double positionY = 2d * (renderer.getWindow().getHeight() - mouseY) / (double) renderer.getWindow().getHeight() - 1d;

        //into frustum. vectors times -z=w to get into homogeneous space
        Vector4 rayOrigin4 = new Vector4(positionX,positionY,0,1);
        Vector4 rayDestination4 = new Vector4(positionX, positionY, 1, 1);

        // run them backwards through rendering pipeline
        Matrix4 invertedPipeline = renderer.getInvertedPipeline(scene);
        Vector3 rayOrigin = invertedPipeline.times(rayOrigin4).standardize().extractVector3();
        Vector3 rayDestination = invertedPipeline.times(rayDestination4).standardize().extractVector3();

        Vector3 rayDirection = rayDestination.minus(rayOrigin).normalize();

        // here we could construct an "arrow" to help us visualize the ray cast from the mouse
        //scene.setArrow(rayOrigin, rayDestination);

        return Data.getPlanet().getPlanetObject().getIntersectedFacePart(rayOrigin, rayDirection);
    }

    public static GuiElement getGuiUnderMouse(double mouseX, double mouseY) {
        return GuiData.getHud().getElementUnderMouse(mouseX, mouseY);
    }

}
