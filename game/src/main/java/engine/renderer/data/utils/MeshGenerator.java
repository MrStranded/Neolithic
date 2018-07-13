package engine.renderer.data.utils;

import engine.renderer.data.Mesh;
import engine.renderer.data.Texture;

public class MeshGenerator {

	// Icosahedron constants
	private static final float HEIGHT = 1f;
	private static final float PHI = 0.5f * (1f + (float) Math.sqrt(5f));
	private static final float ALPHA = 2f * (float) Math.atan(1f / PHI);
	private static final float RADIUS = HEIGHT * (float) Math.sin(ALPHA);
	private static final float Y = HEIGHT * (float) Math.cos(ALPHA); // y position of upper/lower ring
	private static final float ANGLE = 2f * (float) Math.PI / 5f;
	private static final float HALFANGLE = ANGLE / 2f;

	public static Mesh createQuad(Texture texture) {

		float[] vertices = {
				-0.5f,  -0.5f,  0,  // left bottom
				0.5f,   -0.5f,  0,  // right bottom
				-0.5f,  0.5f,   0,  // left top
				0.5f,   0.5f,   0   // right top
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
		vertices[10*3 + 1] = HEIGHT;
		vertices[11*3 + 1] = -HEIGHT;

		for (int i=0; i<5; i++) {
			// up - vertices 0 to 4
			vertices[i*3 + 0] = RADIUS * (float) Math.cos(i * ANGLE);
			vertices[i*3 + 1] = Y;
			vertices[i*3 + 2] = RADIUS * (float) Math.sin(i * ANGLE);

			// down - vertices 5 to 9
			vertices[5*3 + i*3 + 0] = RADIUS * (float) Math.cos(i * ANGLE - HALFANGLE);
			vertices[5*3 + i*3 + 1] = -Y;
			vertices[5*3 + i*3 + 2] = RADIUS * (float) Math.sin(i * ANGLE - HALFANGLE);
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
