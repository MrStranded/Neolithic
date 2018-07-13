package engine.renderer.data.utils;

import engine.renderer.data.Mesh;
import engine.renderer.data.Texture;

public class MeshGenerator {

	// Icosahedron constants
	private static final float height = 1f;
	private static final float phi = 0.5f * (1f + (float) Math.sqrt(5f));
	private static final float alpha = 2f * (float) Math.atan(1f / phi);
	private static final float radius = height * (float) Math.sin(alpha);
	private static final float y = height * (float) Math.cos(alpha); // y position of upper/lower ring
	private static final float angle = 2f * (float) Math.PI / 5f;
	private static final float halfAngle = angle / 2f;

	public static Mesh createQuad(double size, Texture texture) {

		float s = (float) size / 2f;

		float[] vertices = {
				-s,  -s,  0,  // left bottom
				s,   -s,  0,  // right bottom
				-s,  s,   0,  // left top
				s,   s,   0   // right top
		};

		int[] indices = {
				0,1,2,
				1,3,2
		};

		float[] colors = {
				0.0f, 1.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 1.0f,
		};

		float[] textureCoordinates = {
				0f, 1f,
				1f, 1f,
				0f, 0f,
				1f, 0f
		};

		return new Mesh(vertices, indices, colors, texture, textureCoordinates);
	}

	public static Mesh createIcosahedron(Texture texture) {

		// ------------------------------------- vertices
		float[] vertices = new float[12*3];

		// 10th vertex is north pole, 11th vertex is south pole
		vertices[10*3 + 1] = height;
		vertices[11*3 + 1] = -height;

		for (int i=0; i<5; i++) {
			// up - vertices 0 to 4
			vertices[i*3 + 0] = radius * (float) Math.cos(i * angle);
			vertices[i*3 + 1] = y;
			vertices[i*3 + 2] = radius * (float) Math.sin(i * angle);

			// down - vertices 5 to 9
			vertices[5*3 + i*3 + 0] = radius * (float) Math.cos(i * angle - halfAngle);
			vertices[5*3 + i*3 + 1] = -y;
			vertices[5*3 + i*3 + 2] = radius * (float) Math.sin(i * angle - halfAngle);
		}

		// ------------------------------------- indices
		int[] indices = new int[20*3];

		for (int i=0; i<5; i++) {
			// layer 0
			indices[i*3 + 0] = 11; // south
			indices[i*3 + 1] = 5 + i; // down k
			indices[i*3 + 2] = 5 + ((i+1) % 5); // down k+1

			// layer 1
			indices[5*3 + i*3 + 0] = 5 + i; // down
			indices[5*3 + i*3 + 1] = i; // up k
			indices[5*3 + i*3 + 2] = 5 + ((i+1) % 5); // down k+1

			// layer 2
			indices[10*3 + i*3 + 0] = 5 + ((i+1) % 5); // down k+1
			indices[10*3 + i*3 + 1] = i; // up k
			indices[10*3 + i*3 + 2] = (i+1) % 5; // up k+1

			// layer 3
			indices[15*3 + i*3 + 0] = i; // up k
			indices[15*3 + i*3 + 1] = 10; // north
			indices[15*3 + i*3 + 2] = (i+1) % 5; // up k+1
		}

		// ------------------------------------- colors
		float[] colors = new float[12*3];

		for (int i=0; i<colors.length; i++) {
			colors[i] = (float) Math.random();
		}

		// ------------------------------------- texture coordinates
		float[] textureCoordniates = new float[12*2];

		for (int i=0; i<24; i++) {
			textureCoordniates[i] = (float) Math.random();
		}

		return new Mesh(vertices, indices, colors, texture, textureCoordniates);
	}
}
