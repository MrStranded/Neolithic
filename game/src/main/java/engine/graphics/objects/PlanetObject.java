package engine.graphics.objects;

import engine.data.Planet;
import engine.graphics.objects.generators.PlanetGenerator;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;
import engine.math.numericalObjects.Vector4;
import org.lwjgl.system.CallbackI;

public class PlanetObject extends GraphicalObject {

	private Planet planet;

	private Vector3[] faceNormals;
	private Vector3 cameraDirection = new Vector3(0,0,1);
	private Matrix4 viewMatrix;

	private int depth = 3;

	public PlanetObject(Planet planet) {
		this.planet = planet;

		createLODMesh();
	}

	private void createLODMesh() {
		compositeMesh = new CompositeMesh(depth);

		faceNormals = PlanetGenerator.createFaceNormals();

		int s = planet.getSize();
		for (int i=depth-1; i>=0; i--) {
			compositeMesh.setSubMesh(PlanetGenerator.createPlanet(s),i);
			s /= 2;
		}
	}

	public void render(ShaderProgram shaderProgram, boolean sendMaterial) {
		Vector3 viewVector;

		for (int i=0; i<20; i++) {
			viewVector = viewMatrix.times(getWorldMatrix()).times(new Vector4(faceNormals[i],0)).extractVector3().normalize();

			double factor = viewVector.dot(cameraDirection);
			if (factor < 0) {
				factor = 0;
			}

			int detailLevel = (int) ((double) depth*factor);
			if (detailLevel >= depth) {
				System.out.println("over it"); // should never happen
				detailLevel = depth-1;
			}

			compositeMesh.get(detailLevel).get(i).render(shaderProgram, sendMaterial, useDepthTest);
		}
	}

	public void setViewMatrix(Matrix4 viewMatrix) {
		this.viewMatrix = viewMatrix;
	}
}
