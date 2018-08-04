package engine.graphics.objects.generators;

import constants.GraphicalConstants;
import engine.graphics.objects.planet.CompositeMesh;
import engine.graphics.objects.models.Mesh;
import engine.graphics.objects.planet.FacePart;
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

		// a makin' it a round
		if (true) {
			corner1 = corner1.normalize();
			corner2 = corner2.normalize();
			corner3 = corner3.normalize();
		}

		double f = 1d + Math.random()*0.125d;

		corner1 = corner1.times(f);
		corner2 = corner2.times(f);
		corner3 = corner3.times(f);

		Vector3[] vectorArray = {corner1, corner2, corner3, origin, corner1, corner2, corner3};

		// ------------------------------------- vertices
		float[] vertices = VectorConverter.Vector3ArrayToFloatArray(vectorArray);

		// ------------------------------------- indices
		int[] indices = {
				0, 1, 2,
				3, 5, 4,
				3, 6, 5,
				3, 4, 6
		};

		// ------------------------------------- normals

		Vector3 normal = corner1.plus(corner2).plus(corner3).normalize();

		double normalFactor = 1d / GraphicalConstants.PLANET_CONSTRUCTION_SIDE_NORMAL_QUOTIENT;
		Vector3 mid = corner1.plus(corner2).plus(corner3).times(1d/3d);
		Vector3[] midToCorner = new Vector3[3];
		midToCorner[0] = corner1.minus(mid).plus(normal.times(normalFactor)).normalize();
		midToCorner[1] = corner2.minus(mid).plus(normal.times(normalFactor)).normalize();
		midToCorner[2] = corner3.minus(mid).plus(normal.times(normalFactor)).normalize();

		float[] normals = new float[7*3];
		// top
		for (int i=0; i<3; i++) {
			normals[i*3 + 0] = (float) normal.getX();
			normals[i*3 + 1] = (float) normal.getY();
			normals[i*3 + 2] = (float) normal.getZ();
		}

		// origin
		normals[3*3 + 0] = (float) normal.getX();
		normals[3*3 + 1] = (float) normal.getY();
		normals[3*3 + 2] = (float) normal.getZ();

		// sides
		for (int i=0; i<3; i++) {
			normals[(4+i)*3 + 0] = (float) midToCorner[i].getX();
			normals[(4+i)*3 + 1] = (float) midToCorner[i].getY();
			normals[(4+i)*3 + 2] = (float) midToCorner[i].getZ();
		}

		// ------------------------------------- texture coordinates
		float[] textureCoordniates = {
				0, 0,
				0.5f, 1f,
				1f, 0,
				1f, 1f
		};

		Mesh tile = new Mesh(vertices, indices, normals, textureCoordniates);
		tile.setColor(
				(float) (0.5d + 0.5d*Math.random()),
				(float) (0.5d + 0.5d*Math.random()),
				(float) (0.5d + 0.5d*Math.random())
		);
		return tile;
	}

	private static FacePart createFace(Vector3 corner1, Vector3 corner2, Vector3 corner3, int size, int depth) {
		Vector3 normal = corner1.plus(corner2).plus(corner3).normalize();

		Mesh faceMesh = createTile(corner1, corner2, corner3);

		FacePart face = new FacePart();
		face.setNormal(normal);
		face.setMesh(faceMesh);
		face.setDepth(depth);

		int newSize = size / 2;

		if (newSize > 0) {
			Vector3 edgeX = corner2.minus(corner1);
			Vector3 edgeY = corner3.minus(corner1);

			Vector3 dx = edgeX.times(0.5d);
			Vector3 dy = edgeY.times(0.5d);

			Vector3 mid = corner1.plus(edgeX.plus(edgeY).times(0.5d));

			FacePart[] subFaces = new FacePart[4];

			subFaces[0] = createFace(
					corner1,
					corner1.plus(dx),
					corner1.plus(dy),
					newSize,
					depth + 1
			);
			subFaces[1] = createFace(
					corner1.plus(dx),
					corner1.plus(dx).plus(dx),
					corner1.plus(dy).plus(dx),
					newSize,
					depth + 1
			);
			subFaces[2] = createFace(
					corner1.plus(dy),
					corner1.plus(dx).plus(dy),
					corner1.plus(dy).plus(dy),
					newSize,
					depth + 1
			);
			subFaces[3] = createFace(
					corner1.plus(dx),
					corner1.plus(dx).plus(dy),
					corner1.plus(dy),
					newSize,
					depth + 1
			);

			face.setQuarterFaces(subFaces);
		}

		return face;
	}

	// ###################################################################################
	// ################################ Planet ###########################################
	// ###################################################################################

	public static FacePart[] createPlanet(int size) {
		FacePart[] faces = new FacePart[20];

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

					int face = (y*2 + f) * 5 + x;
					faces[face] = createFace(corner1, corner2, corner3, size, 1);
				}
			}
		}

		return faces;
	}
}
