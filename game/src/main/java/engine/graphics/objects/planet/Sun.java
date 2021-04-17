package engine.graphics.objects.planet;


import engine.graphics.objects.GraphicalObject;
import engine.graphics.objects.movement.SunDependantObject;
import engine.math.numericalObjects.Vector3;

import java.util.ArrayList;
import java.util.List;

public class Sun {

    private GraphicalObject sun;
    private List<Companion> companions = new ArrayList<>();
    private Vector3 normalizedSunPosition = Vector3.ZERO;

    public Sun(GraphicalObject sun) {
        this.sun = sun;
        addCompanion(sun, 1);
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
     * @param leAngleStep
     */
    public void changeAngle(double leAngleStep) {
        final double angleStep = leAngleStep*Math.PI/180d;
        companions.forEach(c -> c.changeAngle(-angleStep));

        updateSunPosition();
    }

    private void resetPositions() {
        companions.forEach(Companion::reset);
    }

    private void updateSunPosition() {
        normalizedSunPosition = sun.getPosition().normalize();
    }
    public Vector3 getNormalizedSunPosition() {
        return normalizedSunPosition;
    }

    public void addCompanion(SunDependantObject companion, double rotationFactor) {
        companions.add(new Companion(companion, rotationFactor));
    }

    private class Companion {
        SunDependantObject companion;
        double factor;

        Companion(SunDependantObject companion, double factor) {
            this.companion = companion;
            this.factor = factor;
        }

        void changeAngle(double angleStep) {
            companion.sunAngleIncrement(angleStep * factor);
        }

        void reset() {
            companion.sunAngleReset();
        }
    }

    public GraphicalObject getSunObject() {
        return sun;
    }

}
