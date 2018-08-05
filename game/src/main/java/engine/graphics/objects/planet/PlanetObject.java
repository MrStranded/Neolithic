package engine.graphics.objects.planet;

import engine.data.Planet;
import engine.graphics.objects.generators.PlanetGenerator;
import engine.graphics.objects.movement.MoveableObject;
import engine.graphics.objects.planet.CompositeMesh;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;
import engine.math.numericalObjects.Vector4;

public class PlanetObject extends MoveableObject {

	private Planet planet;

	private FacePart[] faceParts;

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

		createLODMesh();
	}

	private void createLODMesh() {
		faceParts = PlanetGenerator.createPlanet(planet);
	}

	public void render(ShaderProgram shaderProgram, Matrix4 viewMatrix, boolean putDataIntoShader) {
		Matrix4 viewWorldMatrix = viewMatrix.times(getWorldMatrix());

		if (faceParts != null) {
			for (int i=0; i<20; i++) {
				if (faceParts[i] != null) {
					faceParts[i].render(shaderProgram, viewWorldMatrix, depth, putDataIntoShader);
				}
			}
		}
	}
}
