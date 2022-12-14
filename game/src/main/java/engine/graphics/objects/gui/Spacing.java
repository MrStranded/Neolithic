package engine.graphics.objects.gui;

import engine.parser.utils.Logger;

public class Spacing {

    private static final int TOP       = 0;
    private static final int RIGHT     = 1;
    private static final int BOTTOM    = 2;
    private static final int LEFT      = 3;

    private double[] spacing;

    public Spacing(double[] spacing) {
        if (spacing == null || spacing.length != 4) {
            Logger.error("Spacing has to be initialized with an array with the exact size of 4. Instead was: " + spacing);
            spacing = new double[] {0, 0, 0, 0};
            return;
        }

        this.spacing = spacing;
    }

    public double getTop() {
        return spacing[TOP];
    }

    public double getRight() {
        return spacing[RIGHT];
    }

    public double getBottom() {
        return spacing[BOTTOM];
    }

    public double getLeft() {
        return spacing[LEFT];
    }

    // ###################################################################################
    // ################################ Printing #########################################
    // ###################################################################################

    public String toString() {
        return "(top: " + getTop() + " ,right: " + getRight() + " ,bottom: " + getBottom() + " ,left: " + getLeft() + ")";
    }
}
