package engine.graphics.objects.planet;


import constants.GraphicalConstants;
import engine.graphics.objects.GraphicalObject;
import engine.graphics.objects.light.DirectionalLight;
import engine.graphics.objects.light.PointLight;
import engine.graphics.objects.light.ShadowMap;
import engine.math.numericalObjects.Vector3;

public class Sun {

    double angle = 0;

    GraphicalObject sunObject;
    DirectionalLight directionalLight;
    PointLight pointLight;
    ShadowMap shadowMap;

    public Sun(GraphicalObject sunObject, DirectionalLight directionalLight, PointLight pointLight, ShadowMap shadowMap) {
        this.sunObject = sunObject;
        this.directionalLight = directionalLight;
        this.pointLight = pointLight;
        this.shadowMap = shadowMap;
    }

    /**
     * Sets the angle of the sun to the given value in degrees.
     * @param angle
     */
    public void setAngle(double angle) {
        resetPositions();
        changeAngle(angle);
    }

    /**
     * Changes the angle of the sun by the given value in degrees.
     * @param angleStep
     */
    public void changeAngle(double angleStep) {
        angleStep = angleStep*Math.PI/180d;
        angle += angleStep;

        sunObject.rotateYAroundOrigin(-angleStep);
        directionalLight.rotateY(-angleStep);
        pointLight.rotateYAroundOrigin(-angleStep);

        shadowMap.setLightAngle(-angle);
        shadowMap.cameraChangedPosition();
    }

    private void resetPositions() {
        angle = 0;

        sunObject.setPosition(0,0, GraphicalConstants.SUN_DISTANCE);
        pointLight.setPosition(0,0, GraphicalConstants.SUN_DISTANCE);
        directionalLight.setDirection(new Vector3(0,0,-1));

        shadowMap.setLightAngle(0);
        shadowMap.cameraChangedPosition();
    }

    public GraphicalObject getGraphicalObject() {
        return sunObject;
    }

}
