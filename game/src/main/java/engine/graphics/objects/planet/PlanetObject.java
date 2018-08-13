package engine.graphics.objects.planet;

import engine.data.planetary.Planet;
import engine.graphics.objects.generators.PlanetGenerator;
import engine.graphics.objects.models.Material;
import engine.graphics.objects.movement.MoveableObject;
import engine.graphics.renderer.color.RGBA;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.math.numericalObjects.Matrix4;

public class PlanetObject extends MoveableObject {

	private Planet planet;

	private FacePart[] faceParts;
	private PlanetGenerator planetGenerator;

	private int depth = 0;

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
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public Planet getPlanet() {
		return planet;
	}

	public FacePart[] getFaceParts() {
		return faceParts;
	}
}
