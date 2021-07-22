package engine.graphics.gui;

public class RelativeParentPosition {

	public static final int LEFT = 0;
	public static final int TOP = 0;
	public static final int CENTER = 1;
	public static final int RIGHT = 2;
	public static final int BOTTOM = 2;

	public static double getAbsOriginX(double windowWidth, double objectWidth, int position) {
		switch (position) {
			case CENTER: return (windowWidth - objectWidth) / 2;
			case RIGHT: return windowWidth - objectWidth;
			default: return 0;
		}
	}

	public static double getAbsOriginY(double windowHeight, double objectHeight, int position) {
		switch (position) {
			case CENTER: return (windowHeight - objectHeight) / 2;
			case BOTTOM: return windowHeight - objectHeight;
			default: return 0;
		}
	}
}
