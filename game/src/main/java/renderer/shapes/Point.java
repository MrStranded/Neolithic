package renderer.shapes;

import math.Vector3;
import renderer.color.RGBA;

/**
 * Points are graphical objects, that only consist of a position and a color.
 */
public class Point {

	private Vector3 position;
	private RGBA color;

	public Point(Vector3 position) {
		this.position = position;
		color = new RGBA(1,1,1);
	}

	public Point(Vector3 position ,RGBA color) {
		this.position = position;
		this.color = color;
	}

}
