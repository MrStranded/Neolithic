package engine.graphics.objects.generators;

import engine.graphics.objects.CompositeMesh;
import engine.graphics.objects.models.Mesh;
import engine.math.numericalObjects.Vector3;
import engine.utils.converters.VectorConverter;

public class PlanetGenerator {

	// Icosahedron constants
	private static final double HEIGHT = 1f;
	private static final double PHI = 0.5f * (1f + Math.sqrt(5f));
	private static final double ALPHA = 2f * Math.atan(1f / PHI);
	private static final double RADIUS = HEIGHT * Math.sin(ALPHA);
	private static final double Y = HEIGHT * Math.cos(ALPHA); // y position of upper/lower ring
	private static final double ANGLE = 2f * Math.PI / 5f;
	private static final double HALFANGLE = ANGLE / 2f;

	// ###################################################################################
	// ################################ Construction Helpers #############################
	// ###################################################################################

	private static Vector3 getCorner(int x, int y) {
		switch (y) {
			case 0: // south pole
				return new Vector3(0, -HEIGHT,0);
			case 1: // lower ring
				return new Vector3(RADIUS * Math.cos(-x * ANGLE + HALFANGLE), -Y, RADIUS * Math.sin(-x * ANGLE + HALFANGLE));
			case 2: // upper ring
				return new Vector3(RADIUS * Math.cos(-x * ANGLE), Y, RADIUS * Math.sin(-x * ANGLE));
			case 3: // north pole
				return new Vector3(0, HEIGHT, 0);
		}
		return new Vector3(0,0,0);
	}

	private static Mesh createTile(Vector3 corner1, Vector3 corner2, Vector3 corner3) {
		Vector3 origin = new Vector3(0,0,0);

		corner1 = corner1.normalize();
		corner2 = corner2.normalize();
		corner3 = corner3.normalize();

		Vector3[] top = {corner1, corner2, corner3};

		// ------------------------------------- vertices
		float[] vertices = VectorConverter.Vector3ArrayToFloatArray(top);

		// ------------------------------------- indices
		int[] indices = {
				0, 1, 2
		};

		// ------------------------------------- normals

		// edgy
		
		Vector3 normal = corner1.plus(corner2).plus(corner3).normalize();
		float[] normals = new float[3*3];
		for (int i=0; i<3; i++) {
			normals[i*3 + 0] = (float) normal.getX();
			normals[i*3 + 1] = (float) normal.getY();
			normals[i*3 + 2] = (float) normal.getZ();
		}

		// smooth

		//float[] normals = createOutwardFacingNormals(vertices);

		// ------------------------------------- texture coordinates
		float[] textureCoordniates = {
				0, 0,
				0.5f, 1f,
				1f, 0
		};

		return new Mesh(vertices, indices, normals, textureCoordniates);
	}

	private static CompositeMesh createFace(int size, Vector3 corner1, Vector3 corner2, Vector3 corner3) {
		Vector3 edgeX = corner2.minus(corner1);
		Vector3 edgeY = corner3.minus(corner1);

		Vector3 dx = edgeX.times(1d/(double) size);
		Vector3 dy = edgeY.times(1d/(double) size);

		Vector3 mid = corner1.plus(edgeX.plus(edgeY).times(0.5d));

		CompositeMesh tiles = new CompositeMesh(size*size);

		for (int y=0; y<size; y++) {
			for (int x=0; x<size; x++) {
				Vector3 position;
				if (x+y < size) { // normal tiles
					position = corner1.plus(dx.times(x)).plus(dy.times(y));
					tiles.setSubMesh(new CompositeMesh(createTile(
							position,
							position.plus(dx),
							position.plus(dy)
					)), y*size + x);
				} else { // hidden tiles
					position = corner1.plus(dx.times(x)).plus(dy.times(y));
					position = mid.plus(mid.minus(position));
					tiles.setSubMesh(new CompositeMesh(createTile(
							position,
							position.minus(dx),
							position.minus(dy)
					)), y*size + x);

				}
			}
		}

		//CompositeMesh tiles = new CompositeMesh(createTile(corner1, corner1.plus(dx), corner1.plus(dy)));

		return tiles;
	}

	// ###################################################################################
	// ################################ Planet ###########################################
	// ###################################################################################

	public static CompositeMesh createPlanet(int size) {
		CompositeMesh faces = new CompositeMesh(20);

		for (int y=0; y<2; y++) { // upper lower ring
			for (int f=0; f<2; f++) { // flip
				for (int x=0; x<5; x++) { // around globe
					Vector3 corner1, corner2, corner3;

					if (f == 0) { // flipped down
						corner1 = getCorner(x + 1, y);
						corner2 = getCorner(x + 1, y + 1);
						corner3 = getCorner(x, y + 1);

					} else { // flipped up
						corner1 = getCorner(x, y + 1);
						corner2 = getCorner(x + 1, y + 1);
						corner3 = getCorner(x, y + 2);

					}

					faces.setSubMesh(createFace(size, corner1, corner2, corner3), (y*2 + f) * 5 + x);
				}
			}
		}

		return faces;
	}

	private static float[] createOutwardFacingNormals(float[] vertices) {
		float[] normals = new float[vertices.length];

		for (int i=0; i<normals.length/3; i++) {

			Vector3 normal = new Vector3(vertices[i*3 + 0], vertices[i*3 + 1], vertices[i*3 + 2]);
			try {
				normal = normal.normalize();
			} catch (Exception e) {
				e.printStackTrace();
			}
			normals[i*3 + 0] = (float) normal.getX();
			normals[i*3 + 1] = (float) normal.getY();
			normals[i*3 + 2] = (float) normal.getZ();
		}

		return normals;
	}
}
