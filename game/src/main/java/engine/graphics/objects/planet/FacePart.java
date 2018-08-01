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

	// ###################################################################################
	// ################################ Rendering ########################################
	// ###################################################################################

	private void renderSelf(ShaderProgram shaderProgram, boolean putDataIntoShader) {
		if (putDataIntoShader) {
			shaderProgram.setUniform("color", mesh.getColor());
			if (mesh.getMaterial() != null) {
				shaderProgram.setUniform("material", mesh.getMaterial());
			}
		}

		mesh.render(true);
	}
	
	public void render(ShaderProgram shaderProgram, Matrix4 viewWorldMatrix, int depth, boolean putDataIntoShader) {
		// performance!!!
		if (quarterFaces == null) {
			renderSelf(shaderProgram, putDataIntoShader);
			return;
		}

		Vector3 viewVector = viewWorldMatrix.times(new Vector4(normal,0)).extractVector3();//.normalize();
		Vector3 distanceVector = viewWorldMatrix.times(new Vector4(normal, 1)).extractVector3();
		
		double factor = viewVector.dot(cameraDirection);
		if (factor < 0) {
			factor = 0;
		}

		// effect extremification and normalization
		factor = factor * factor / viewVector.lengthSquared();
		
		// distance detail falloff
		double distanceQuotient = distanceVector.lengthSquared();
		if (distanceQuotient < 1d) {
			distanceQuotient = 1d;
		}
		
		factor = factor / distanceQuotient;

		int detailLevel = (int) ((double) depth*factor);
		if (detailLevel >= depth) {
			detailLevel = depth-1;
		}

		if ((detailLevel >= this.depth) && (quarterFaces != null)) {
			for (FacePart facePart : quarterFaces) {
				if (facePart != null) {
					facePart.render(shaderProgram, viewWorldMatrix, depth, putDataIntoShader);
				}
			}
		} else {
			renderSelf(shaderProgram, putDataIntoShader);
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
