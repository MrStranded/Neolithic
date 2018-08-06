package engine.graphics.objects.generators;

import constants.GraphicalConstants;
import constants.TopologyConstants;
import engine.data.Planet;
import engine.data.Tile;
import engine.graphics.objects.planet.CompositeMesh;
import engine.graphics.objects.models.Mesh;
import engine.graphics.objects.planet.FacePart;
import engine.logic.Neighbour;
import engine.math.numericalObjects.Vector3;
import engine.utils.converters.IntegerConverter;
import engine.utils.converters.VectorConverter;

import java.util.ArrayList;
import java.util.List;

public class PlanetGenerator {

	// Icosahedron constants
	private static final double HEIGHT = 1f;
	private static final double PHI = 0.5f * (1f + Math.sqrt(5f));
	private static final double ALPHA = 2f * Math.atan(1f / PHI);
	private static final double RADIUS = HEIGHT * Math.sin(ALPHA);
	private static final double Y = HEIGHT * Math.cos(ALPHA); // y position of upper/lower ring
	private static final double ANGLE = 2f * Math.PI / 5f;
	private static final double HALFANGLE = ANGLE / 2f;

	private static Planet planet;

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

	private static double getHeightFactor(double height) {
		return (TopologyConstants.PLANET_MINIMUM_HEIGHT + height) / TopologyConstants.PLANET_MINIMUM_HEIGHT;
	}

	// ###################################################################################
	// ################################ Tile #############################################
	// ###################################################################################

	private static Mesh createTile(Vector3 corner1, Vector3 corner2, Vector3 corner3, double height, Tile tile, boolean smallest) {
		double f = getHeightFactor(height);

		Vector3[] upper = new Vector3[3];

		upper[0] = corner1.times(f);
		upper[1] = corner2.times(f);
		upper[2] = corner3.times(f);

		Vector3 normal = corner1.plus(corner2).plus(corner3).normalize();
		Vector3 mid = corner1.plus(corner2).plus(corner3).times(1d/3d);

		double normalFactor = 1d / GraphicalConstants.PLANET_CONSTRUCTION_SIDE_NORMAL_QUOTIENT;

		Vector3[] midToCorner = new Vector3[3];
		midToCorner[0] = corner1.minus(mid).plus(normal.times(normalFactor)).normalize();
		midToCorner[1] = corner2.minus(mid).plus(normal.times(normalFactor)).normalize();
		midToCorner[2] = corner3.minus(mid).plus(normal.times(normalFactor)).normalize();

		// ------------------------------------- vertices set up (top triangle)
		List<Vector3> vectorList = new ArrayList<>(15);
		vectorList.add(upper[0]);
		vectorList.add(upper[1]);
		vectorList.add(upper[2]);

		// ------------------------------------- indices set up (top triangle)
		List<Integer> indicesList = new ArrayList<>(15);
		indicesList.add(0);
		indicesList.add(1);
		indicesList.add(2);

		// ------------------------------------- normals set up (top triangle)
		List<Vector3> normalList = new ArrayList<>(15);
		normalList.add(normal);
		normalList.add(normal);
		normalList.add(normal);

		// ------------------------------------- neighbour retrieval and side mesh build up
		Tile[] neighbours = Neighbour.getNeighbours(tile);

		int index = 3;
		Vector3[] lower = new Vector3[3];

		for (int i=0; i<3; i++) {
			double otherHeight = neighbours[i].getHeight();
			if (!smallest) {
				otherHeight = 0;
			}

			if (otherHeight < tile.getHeight()) {
				double lowerFactor = getHeightFactor(otherHeight);

				lower[0] = corner1.times(lowerFactor);
				lower[1] = corner2.times(lowerFactor);
				lower[2] = corner3.times(lowerFactor);

				int first = i-1;
				int last = i;
				if (first < 0) {
					first += 3;
				}

				// side mesh vertices
				vectorList.add(upper[first]);
				vectorList.add(lower[first]);
				vectorList.add(upper[last]);
				vectorList.add(lower[last]);

				// side mesh normals
				normalList.add(midToCorner[first]);
				normalList.add(midToCorner[first]);
				normalList.add(midToCorner[last]);
				normalList.add(midToCorner[last]);

				// side mesh indices
				indicesList.add(index);
				indicesList.add(index + 1);
				indicesList.add(index + 2);
				indicesList.add(index + 1);
				indicesList.add(index + 3);
				indicesList.add(index + 2);

				index += 6;
			}
		}

		// ------------------------------------- vertices
		float[] vertices = VectorConverter.Vector3ListToFloatArray(vectorList);

		// ------------------------------------- indices
		int[] indices = IntegerConverter.IntegerListToIntArray(indicesList);

		// ------------------------------------- normals
		float[] normals = VectorConverter.Vector3ListToFloatArray(normalList);

		// ------------------------------------- texture coordinates
		float[] textureCoordniates = new float[vertices.length]; // no texture support for planets so far

		Mesh tileMesh = new Mesh(vertices, indices, normals, textureCoordniates);
		tileMesh.setColor(
				(float) (0.5d + 0.5d * height / TopologyConstants.PLANET_MAXIMUM_HEIGHT),
				(float) (0.5d + 0.25d * height / TopologyConstants.PLANET_MAXIMUM_HEIGHT),
				(float) (0.5d + 0.125d * height / TopologyConstants.PLANET_MAXIMUM_HEIGHT)
		);
		return tileMesh;
	}

	// ###################################################################################
	// ################################ Face #############################################
	// ###################################################################################

	private static FacePart createFace(Vector3 corner1, Vector3 corner2, Vector3 corner3, int size, int depth, int facePos, int tileX, int tileY) {
		corner1 = corner1.normalize();
		corner2 = corner2.normalize();
		corner3 = corner3.normalize();

		FacePart face = new FacePart(corner1, corner2, corner3);
		face.setDepth(depth);

		int newSize = size / 2;

		if (newSize > 0) {
			Vector3 edgeX = corner2.minus(corner1);
			Vector3 edgeY = corner3.minus(corner1);

			Vector3 dx = edgeX.times(0.5d);
			Vector3 dy = edgeY.times(0.5d);

			FacePart[] subFaces = new FacePart[4];

			subFaces[0] = createFace(
					corner1,
					corner1.plus(dx),
					corner1.plus(dy),
					newSize,
					depth + 1,
					facePos, tileX, tileY
			);
			subFaces[1] = createFace(
					corner1.plus(dx),
					corner1.plus(dx).plus(dx),
					corner1.plus(dy).plus(dx),
					newSize,
					depth + 1,
					facePos, tileX + newSize, tileY
			);
			subFaces[2] = createFace(
					corner1.plus(dy),
					corner1.plus(dx).plus(dy),
					corner1.plus(dy).plus(dy),
					newSize,
					depth + 1,
					facePos, tileX, tileY + newSize
			);
			subFaces[3] = createFace(
					corner1.plus(dx).plus(dy),
					corner1.plus(dy),
					corner1.plus(dx),
					newSize,
					depth + 1,
					facePos, planet.getSize() - newSize - tileX, planet.getSize() - newSize - tileY
			);

			face.setQuarterFaces(subFaces);

			double height = 0d;
			for (int i=0; i<4; i++) {
				if (subFaces[i] != null) {
					if (subFaces[i].getHeight() > height) {
						height = subFaces[i].getHeight();
					}
				}
			}
			face.setHeight(height);

		} else {
			face.setHeight(planet.getFace(facePos).getTile(tileX, tileY).getHeight());

		}

		Mesh faceMesh = createTile(corner1, corner2, corner3, face.getHeight(), planet.getFace(facePos).getTile(tileX, tileY), (newSize == 0));
		face.setMesh(faceMesh);

		return face;
	}

	// ###################################################################################
	// ################################ Planet ###########################################
	// ###################################################################################

	public static FacePart[] createPlanet(Planet thePlanet) {
		planet = thePlanet;
		FacePart[] faces = new FacePart[20];

		for (int y=0; y<2; y++) { // upper lower ring
			for (int f=0; f<2; f++) { // flip
				for (int x=0; x<5; x++) { // around globe
					Vector3 corner1, corner2, corner3;

					if (f == 0) { // flipped down
						corner1 = getCorner(x + 1, y + 1);
						corner2 = getCorner(x, y + 1);
						corner3 = getCorner(x + 1, y);

					} else { // flipped up
						corner1 = getCorner(x, y + 1);
						corner2 = getCorner(x + 1, y + 1);
						corner3 = getCorner(x, y + 2);

					}

					int face = (y*2 + f) * 5 + x;
					faces[face] = createFace(corner1, corner2, corner3, planet.getSize(), 1, face, 0, 0);
				}
			}
		}

		return faces;
	}
}
