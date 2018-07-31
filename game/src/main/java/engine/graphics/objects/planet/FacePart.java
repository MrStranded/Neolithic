package engine.graphics.objects.planet;

import engine.graphics.objects.models.Mesh;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;
import engine.math.numericalObjects.Vector4;

public class FacePart {
	
	private static final Vector3 cameraDirection = new Vector3(0,0,1);
	
	private Mesh mesh;
	private Vector3 normal;
	private FacePart[] quarterFaces;

	private int depth;

	public FacePart() {
	}
	
	public void render(ShaderProgram shaderProgram, boolean sendMaterial, Matrix4 viewWorldMatrix, int depth) {
		Vector3 viewVector = viewWorldMatrix.times(new Vector4(normal,0)).extractVector3();//.normalize();
		
		double factor = viewVector.dot(cameraDirection);
		if (factor < 0) {
			factor = 0;
		}

		factor = factor * factor / viewVector.lengthSquared();

		int detailLevel = (int) ((double) depth*factor);
		if (detailLevel >= depth) {
			detailLevel = depth-1;
		}

		//System.out.println(detailLevel + " in " + this.depth + " from " + depth);
		if (detailLevel >= this.depth) {
			if (quarterFaces != null) {
				for (FacePart facePart : quarterFaces) {
					if (facePart != null) {
						facePart.render(shaderProgram, sendMaterial, viewWorldMatrix, depth);
					}
				}
			//} else {
			//	mesh.render(shaderProgram, sendMaterial, true);
			}
		} else {
			mesh.render(shaderProgram, sendMaterial, true);
		}
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public Mesh getMesh() {
		return mesh;
	}
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

	public Vector3 getNormal() {
		return normal;
	}
	public void setNormal(Vector3 normal) {
		this.normal = normal;
	}

	public FacePart[] getQuarterFaces() {
		return quarterFaces;
	}
	public void setQuarterFaces(FacePart[] quarterFaces) {
		this.quarterFaces = quarterFaces;
	}

	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
}
