package engine.graphics.objects;

import engine.graphics.gui.RelativeScreenPosition;
import engine.graphics.objects.models.Mesh;

public class HUDObject extends GraphicalObject {

	private RelativeScreenPosition relativeScreenPosition;

	private double xPos, yPos;
	private double width, height;

	public HUDObject(Mesh mesh) {
		super(mesh);
		mesh.normalize();
	}
	public HUDObject() {}

	public void recalculateScale(double windowWidth, double windowHeight) {

	}
}
