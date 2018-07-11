package utils;

import math.Vector3;
import renderer.shapes.Mesh;

public class MeshGenerator {

	public static Mesh createQuad() {

		Vector3[] points = new Vector3[4];
		points[0] = new Vector3(-0.5d,-0.5d,0d); // left bottom
		points[1] = new Vector3(0.5d,-0.5d,0d);  // right bottom
		points[2] = new Vector3(-0.5d,0.5d,0d); // left top
		points[3] = new Vector3(0.5d,0.5d,0d);  // right top

		Vector3[] vertices = {
				points[0],points[1],points[2],
				points[1],points[3],points[2]
		};

		return new Mesh(vertices);
	}

	public static Mesh createTetrahedron() {

		Vector3[] points = new Vector3[4];
		points[0] = new Vector3(-0.5d,-0.5d,-0.5d); // left back
		points[1] = new Vector3(0.5d,-0.5d,-0.5d);  // right back
		points[2] = new Vector3(0d,-0.6d,0.5d);     // middle front
		points[3] = new Vector3(0d,0.5d,0d);        // top

		Vector3[] vertices = {
				points[0],points[1],points[2],
				points[0],points[3],points[1],
				points[0],points[2],points[3],
				points[1],points[3],points[2]
		};

		return new Mesh(vertices);
	}
}
