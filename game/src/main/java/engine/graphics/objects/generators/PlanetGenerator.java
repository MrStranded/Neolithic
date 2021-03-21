package engine.graphics.objects.generators;

import constants.TopologyConstants;
import engine.data.planetary.Planet;
import engine.data.planetary.Tile;
import engine.graphics.objects.models.Material;
import engine.graphics.objects.models.Mesh;
import engine.graphics.objects.planet.FacePart;
import engine.graphics.objects.planet.PlanetObject;
import engine.graphics.renderer.color.RGBA;
import engine.logic.topology.Neighbour;
import engine.math.numericalObjects.Vector3;
import engine.utils.converters.ColorConverter;
import engine.utils.converters.IntegerConverter;
import engine.utils.converters.VectorConverter;

import java.util.ArrayList;
import java.util.List;

public class PlanetGenerator {

	// Icosahedron constants
	private final double HEIGHT = 1f;
	private final double PHI = 0.5f * (1f + Math.sqrt(5f));
	private final double ALPHA = 2f * Math.atan(1f / PHI);
	private final double RADIUS = HEIGHT * Math.sin(ALPHA);
	private final double Y = HEIGHT * Math.cos(ALPHA); // y position of upper/lower ring
	private final double ANGLE = 2f * Math.PI / 5f;
	private final double HALFANGLE = ANGLE / 2f;

	private PlanetObject planetObject;
	private Planet planet;
	private Material waterMaterial;
	
	public PlanetGenerator(PlanetObject planetObject, Material waterMaterial) {
		this.planetObject = planetObject;
		planet = planetObject.getPlanet();

		this.waterMaterial = waterMaterial;
	}

	// ###################################################################################
	// ################################ Construction Helpers #############################
	// ###################################################################################

	private Vector3 getCorner(int x, int y) {
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

	public static double getHeightFactor(double height) {
		return (TopologyConstants.PLANET_MINIMUM_HEIGHT + height) / TopologyConstants.PLANET_MINIMUM_HEIGHT;
	}

	private double getWeightedHeight(double maxHeight, double sumHeight) {
		return maxHeight/4d + sumHeight*3d/16d;
	}

	// ###################################################################################
	// ################################ Tile #############################################
	// ###################################################################################

	private Mesh createTile(FacePart facePart, Tile tile, boolean smallest) {
		// ------------------------------------- vector value setup
		boolean water = facePart.getWaterHeight() > facePart.getHeight();
		double height = Math.max(facePart.getHeight(), facePart.getWaterHeight());
		double f = PlanetGenerator.getHeightFactor(height);

		Vector3[] upper = new Vector3[3];

		upper[0] = facePart.getCorner1().times(f);
		upper[1] = facePart.getCorner2().times(f);
		upper[2] = facePart.getCorner3().times(f);

		Vector3 positionMid = (upper[0].plus(upper[1]).plus(upper[2])).times(1d/3d);
		if (water) {
			facePart.setWaterMid(positionMid);
			if (facePart.getMid() == null) {
				facePart.setMid(positionMid);
			}
		} else {
			facePart.setMid(positionMid);
		}

		Vector3 mid = facePart.getCorner1().plus(facePart.getCorner2()).plus(facePart.getCorner3()).times(1d/3d);
		Vector3 normal = mid.normalize();

		Vector3[] midToSide = new Vector3[3];
		midToSide[0] = facePart.getCorner1().plus(facePart.getCorner2()).minus(mid).normalize();
		midToSide[1] = facePart.getCorner2().plus(facePart.getCorner3()).minus(mid).normalize();
		midToSide[2] = facePart.getCorner3().plus(facePart.getCorner1()).minus(mid).normalize();

		// ------------------------------------- color value setup
		RGBA topColor, sideColor = null;
		if (water) {
			topColor = TopologyConstants.WATER_DEFAULT_COLOR;
		} else {
			if (facePart.getTopColor() != null) {
				topColor = facePart.getTopColor();
			} else {
				topColor = TopologyConstants.TILE_DEFAULT_COLOR;
				facePart.setTopColor(topColor);
			}
			if (facePart.getSideColor() != null) {
				sideColor = facePart.getSideColor();
			}
		}
		if (sideColor == null) {
			sideColor = topColor.times(TopologyConstants.TILE_SIDE_COLOR_FACTOR);
			facePart.setSideColor(sideColor);
		}

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

		// ------------------------------------- colors set up (top triangle)
		List<RGBA> colorList = new ArrayList<>(15);
		colorList.add(topColor);
		colorList.add(topColor);
		colorList.add(topColor);

		// ------------------------------------- neighbour retrieval and side mesh build up
		Tile[] neighbours = null;
		if (smallest) {
			neighbours = Neighbour.getNeighbours(tile);
		}

		int index = 3;
		Vector3[] lower = new Vector3[3];

		for (int i=0; i<3; i++) {
			double otherHeight = 0;
//			if (smallest) {
//				FacePart otherPart = neighbours[i].getTileMesh();
//				if (otherPart == null) {
//					otherHeight = 0;
//				} else {
//					otherHeight = water
//							? Math.max(otherPart.getHeight(), otherPart.getWaterHeight())
//							: otherPart.getHeight();
//					otherHeight--;
//				}
//			}

			if (otherHeight < height) {
				double lowerFactor = PlanetGenerator.getHeightFactor(otherHeight);

				lower[0] = facePart.getCorner1().times(lowerFactor);
				lower[1] = facePart.getCorner2().times(lowerFactor);
				lower[2] = facePart.getCorner3().times(lowerFactor);

				int first = i;
				int last = i+1;
				if (last >= 3) {
					last -= 3;
				}

				// side mesh vertices
				vectorList.add(upper[first]);
				vectorList.add(lower[first]);
				vectorList.add(upper[last]);
				vectorList.add(lower[last]);

				normalList.add(midToSide[i]);
				normalList.add(midToSide[i]);
				normalList.add(midToSide[i]);
				normalList.add(midToSide[i]);

				colorList.add(sideColor);
				colorList.add(sideColor);
				colorList.add(sideColor);
				colorList.add(sideColor);

				// side mesh indices
				indicesList.add(index);
				indicesList.add(index + 1);
				indicesList.add(index + 2);
				indicesList.add(index + 1);
				indicesList.add(index + 3);
				indicesList.add(index + 2);

				index += 4;
			}
		}

		// ------------------------------------- vertices
		float[] vertices = VectorConverter.Vector3ListToFloatArray(vectorList);

		// ------------------------------------- indices
		int[] indices = IntegerConverter.IntegerListToIntArray(indicesList);

		// ------------------------------------- normals
		float[] normals = VectorConverter.Vector3ListToFloatArray(normalList);

		// ------------------------------------- normals
		float[] colors = ColorConverter.RGBAListToFloatArray(colorList);

		// ------------------------------------- texture coordinates
		float[] textureCoordniates = new float[vertices.length]; // no texture support for planets so far

		Mesh mesh = new Mesh(indices, vertices, textureCoordniates, colors, normals);
		if (water) { // set water material
			mesh.setMaterial(waterMaterial);
		}

		return mesh;
	}

	// ###################################################################################
	// ################################ Face #############################################
	// ###################################################################################

	private FacePart createFace(Vector3 corner1, Vector3 corner2, Vector3 corner3, FacePart superFace, int size, int depth, int facePos, int tileX, int tileY) {
		corner1 = corner1.normalize();
		corner2 = corner2.normalize();
		corner3 = corner3.normalize();

		FacePart facePart = new FacePart(corner1, corner2, corner3, superFace);
		facePart.setDepth(depth);

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
					facePart,
					newSize,
					depth + 1,
					facePos, tileX, tileY
			);
			subFaces[1] = createFace(
					corner1.plus(dx),
					corner1.plus(dx).plus(dx),
					corner1.plus(dy).plus(dx),
					facePart,
					newSize,
					depth + 1,
					facePos, tileX + newSize, tileY
			);
			subFaces[2] = createFace(
					corner1.plus(dy),
					corner1.plus(dx).plus(dy),
					corner1.plus(dy).plus(dy),
					facePart,
					newSize,
					depth + 1,
					facePos, tileX, tileY + newSize
			);
			subFaces[3] = createFace(
					corner1.plus(dx).plus(dy),
					corner1.plus(dy),
					corner1.plus(dx),
					facePart,
					newSize,
					depth + 1,
					facePos, planet.getSize() - newSize - tileX, planet.getSize() - newSize - tileY
			);

			facePart.setQuarterFaces(subFaces);
		}

		Tile tile = planet.getFace(facePos).getTile(tileX, tileY);
		Mesh faceMesh = createTile(facePart, tile, (newSize == 0));
		facePart.setMesh(faceMesh);
		facePart.setTile(tile);
		if (newSize == 0) {
			tile.setTileMesh(facePart);
		}

		return facePart;
	}

	private void updateFace(FacePart facePart) {
		if (! facePart.hasChanged()) { return; }
		facePart.setChanged(false);

		Tile tile = null;
		boolean smallest = false;
		double newHeight, newWaterHeight;

		if (facePart.getQuarterFaces() != null) {
			double maxHeight = 0d;
			double sumHeight = 0d;
			double maxWaterHeight = 0d;
			double sumWaterHeight = 0d;
			RGBA sumTopColor = new RGBA(0,0,0,1);
			RGBA sumSideColor = new RGBA(0,0,0,1);

			for (FacePart subFace : facePart.getQuarterFaces()) {
				if (subFace != null) {
					updateFace(subFace);

					double subHeight = subFace.getHeight();
					double subWaterHeight = subFace.getWaterHeight();

					if (subHeight > maxHeight) { maxHeight = subHeight; }
					if (subWaterHeight > maxWaterHeight) { maxWaterHeight = subWaterHeight; }

					sumHeight += subHeight;
					sumWaterHeight += subWaterHeight;

					sumTopColor = sumTopColor.plus(subFace.getTopColor());
					sumSideColor = sumSideColor.plus(subFace.getSideColor());
				}
			}

			newHeight = (int) getWeightedHeight(maxHeight, sumHeight);
			newWaterHeight = (int) getWeightedHeight(maxWaterHeight, sumWaterHeight);

			facePart.setHeight(newHeight);
			facePart.setWaterHeight(newWaterHeight);

			facePart.setTopColor(sumTopColor.times(0.25d));
			facePart.setSideColor(sumSideColor.times(0.25d));
		} else {
			tile = facePart.getTile();
			smallest = true;

			newHeight = tile.getHeight();//facePart.getHeight();//tile.getHeight();
			newWaterHeight = tile.getWaterHeight();//facePart.getWaterHeight();//tile.getWaterHeight();

			facePart.setHeight(newHeight);
			facePart.setWaterHeight(newWaterHeight);

			facePart.setTopColor(tile.getTopColor());
			facePart.setSideColor(tile.getSideColor());
		}

		//if (newWaterHeight != facePart.getOldWaterHeight() || newHeight != facePart.getOldHeight()) {
			facePart.cleanMeshes();
			Mesh faceMesh = createTile(facePart, tile, smallest);
			if (newWaterHeight > newHeight) {
				facePart.setWaterMesh(faceMesh);
			} else {
				facePart.setMesh(faceMesh);
			}
		//} else {
			// only reset colors here
		//	updateColors(facePart);
		//}
	}

	private void updateColors(FacePart facePart) {
		// ------------------------------------- mesh and water retrieval
		Mesh mesh;
		boolean water = facePart.getWaterHeight() > facePart.getHeight();
		if (water) {
			mesh = facePart.getWaterMesh();
		} else {
			mesh = facePart.getMesh();
		}
		if (mesh == null) {
			return;
		}

		// ------------------------------------- color value setup
		RGBA topColor, sideColor = null;
		if (water) {
			topColor = TopologyConstants.WATER_DEFAULT_COLOR;
		} else {
			if (facePart.getTopColor() != null) {
				topColor = facePart.getTopColor();
			} else {
				topColor = TopologyConstants.TILE_DEFAULT_COLOR;
				facePart.setTopColor(topColor);
			}
			if (facePart.getSideColor() != null) {
				sideColor = facePart.getSideColor();
			}
		}
		if (sideColor == null) {
			sideColor = topColor.times(TopologyConstants.TILE_SIDE_COLOR_FACTOR);
			facePart.setSideColor(sideColor);
		}

		// ------------------------------------- colors set up (top triangle)
		List<RGBA> colorList = new ArrayList<>(15);
		colorList.add(topColor);
		colorList.add(topColor);
		colorList.add(topColor);

		int numSide = mesh.getColors().length/4;

		if (numSide > 3) {
			for (int i = 3; i < numSide; i++) {
				colorList.add(sideColor);
			}
		}

		facePart.setMesh(new Mesh(mesh.getIndices(), mesh.getVertices(), mesh.getTextureCoordinates(), ColorConverter.RGBAListToFloatArray(colorList), mesh.getNormals()));
	}

	// ###################################################################################
	// ################################ Planet ###########################################
	// ###################################################################################

	public FacePart[] createPlanet() {
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
					faces[face] = createFace(corner1, corner2, corner3, null, planet.getSize(), 1, face, 0, 0);
				}
			}
		}

		return faces;
	}

	public void updatePlanet() {
		for (FacePart facePart : planetObject.getFaceParts()) {
			updateFace(facePart);
		}
	}

	public void updateTile(Tile tile) {
		if (tile != null) {
			updateFace(tile.getTileMesh());
		}
	}
}
