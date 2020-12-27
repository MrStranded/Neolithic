package engine.graphics.objects.planet;

import engine.data.planetary.Planet;
import engine.data.planetary.Tile;
import engine.graphics.objects.Scene;
import engine.graphics.objects.generators.PlanetGenerator;
import engine.graphics.objects.models.Material;
import engine.graphics.objects.movement.MoveableObject;
import engine.graphics.renderer.Renderer;
import engine.graphics.renderer.color.RGBA;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;

public class PlanetObject extends MoveableObject {

	private Planet planet;

	private FacePart[] faceParts;
	private PlanetGenerator planetGenerator;

	private int depth;

	public PlanetObject(Planet planet) {
		this.planet = planet;

		depth = 1;
		int s = planet.getSize();
		while (s > 1) {
			s /= 2;
			depth++;
		}

		faceParts = new FacePart[20];

		Material waterMaterial = new Material();
		waterMaterial.setReflectanceStrength(new RGBA(1,1,1,1));
		waterMaterial.setSpecularPower(4);

		planetGenerator = new PlanetGenerator(this, waterMaterial);

		createLODMesh();
	}

	private void createLODMesh() {
		faceParts = planetGenerator.createPlanet();
	}

	public void updateLODMesh() {
		planetGenerator.updatePlanet();
	}
	public void updateLODMesh(Tile tile) {
		planetGenerator.updateTile(tile);
	}

	public void clearChangeFlags() {
		for (FacePart facePart : faceParts) {
			if (facePart.hasChanged()) {
				facePart.clearChangeFlags();
			}
		}
	}

	public void render(ShaderProgram shaderProgram, Matrix4 viewMatrix, boolean putDataIntoShader, boolean drawWater) {
		Matrix4 viewWorldMatrix = viewMatrix.times(getWorldMatrix());

		if (faceParts != null) {
			for (int i=0; i<20; i++) {
				if (faceParts[i] != null) {
					faceParts[i].render(shaderProgram, viewWorldMatrix, depth, putDataIntoShader, false);
				}
			}

			if (drawWater) {
				for (int i=0; i<20; i++) {
					if (faceParts[i] != null) {
						faceParts[i].render(shaderProgram, viewWorldMatrix, depth, putDataIntoShader, true);
					}
				}
			}
		}
	}

	// ###################################################################################
	// ################################ Picking With Mouse ###############################
	// ###################################################################################

	public FacePart getIntersectedFacePart(Vector3 rayOrigin, Vector3 rayDirection) {
		FacePart closest = null;

		for (FacePart facePart : faceParts) {
		    FacePart intersectedPart = facePart.intersects(rayOrigin, rayDirection);
			if (intersectedPart != null) {
				if (closest == null || intersectedPart.closerToCamera(closest, rayOrigin)) {
					closest = intersectedPart;
				}
			}
		}

		if (closest != null) {
			return closest;
		} else {
			System.out.println("planet not clicked");
		}

		return null;
	}

	// ###################################################################################
	// ################################ Picking With Coordinates #########################
	// ###################################################################################

	public Tile getTile(double east, double north) {
		Vector3 origin = Vector3.ZERO;

		Vector3 goal = new Vector3(
				Math.cos(east) * Math.cos(north),
				Math.sin(east) * Math.cos(north),
				Math.sin(north));

		return getIntersectedFacePart(origin, goal).getTile();
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public Planet getPlanet() {
		return planet;
	}

	public FacePart[] getFaceParts() {
		return faceParts;
	}
}
