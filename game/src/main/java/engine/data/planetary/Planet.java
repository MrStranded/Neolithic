package engine.data.planetary;

import engine.graphics.objects.planet.FacePart;
import engine.graphics.objects.planet.PlanetObject;
import engine.math.numericalObjects.Vector3;

import java.util.Arrays;

public class Planet {

	private int size;
	private double radius = 1d;

	private Face[] faces;

	private PlanetObject planetObject;

	public Planet(int size) {
		this.size = size;

		faces = new Face[20];
		for (int y=0; y<4; y++) {
			for (int x=0; x<5; x++) {
				int i = y*5 + x;
				faces[i] = new Face(x, y, this);
			}
		}
	}

	public void generatePlanetMesh() {
		planetObject = new PlanetObject(this);
	}

	public void updatePlanetMesh() {
		for (Face face : faces) {
			for (Tile tile : face.getTiles()) {
				FacePart tileMesh = tile.getTileMesh();
				if (tileMesh != null) {
					tileMesh.setOldHeight(tileMesh.getHeight());
					tileMesh.setOldWaterHeight(tileMesh.getWaterHeight());

					tileMesh.setHeight(tile.getHeight());
					tileMesh.setWaterHeight(tile.getWaterHeight());

					tile.actualizeObjectPosition();
				}
			}
		}
		planetObject.updateLODMesh();
	}
	public void clearChangeFlags() {
		planetObject.clearChangeFlags();
	}

	public PlanetObject getPlanetObject() {
		return planetObject;
	}

	public Face getFace(int x, int y) {
		if (x < 0) {
			x += 5;
		}
		if (x >= 5) {
			x -= 5;
		}
		int i = y*5 + x;
		return faces[i];
	}

	public Face[] getFaces() {
		return faces;
	}

	public Face getFace(int facePosition) {
		return faces[facePosition];
	}

	public int getSize() {
		return size;
	}
}
