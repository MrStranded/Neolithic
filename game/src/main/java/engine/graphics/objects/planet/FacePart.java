package engine.graphics.objects.planet;

import engine.data.Tile;
import engine.graphics.objects.models.Mesh;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;
import engine.math.numericalObjects.Vector4;

public class FacePart {
	
	private static final Vector3 cameraDirection = new Vector3(0,0,1);
	
	private Mesh mesh;
	private Mesh waterMesh;
	private Vector3 normal;
	private FacePart[] quarterFaces;
	private double height;

	private Tile tile;

	private Vector3 corner1, corner2, corner3;

	private int depth;

	public FacePart(Vector3 corner1, Vector3 corner2, Vector3 corner3) {
		this.corner1 = corner1;
		this.corner2 = corner2;
		this.corner3 = corner3;

		normal = corner1.plus(corner2).plus(corner3).normalize();
	}

	// ###################################################################################
	// ################################ Rendering ########################################
	// ###################################################################################

	private void renderSelf(ShaderProgram shaderProgram, boolean putDataIntoShader, boolean drawWater) {
		if (drawWater) {
			if (waterMesh != null) {
				if (putDataIntoShader) {
					shaderProgram.setUniform("color", waterMesh.getColor());
					if (waterMesh.getMaterial() != null) {
						shaderProgram.setUniform("material", waterMesh.getMaterial());
					}
				}
				waterMesh.render(true);
			}

		} else {
			if (putDataIntoShader) {
				shaderProgram.setUniform("color", mesh.getColor());
				if (mesh.getMaterial() != null) {
					shaderProgram.setUniform("material", mesh.getMaterial());
				}
			}
			mesh.render(true);

		}
	}
	
	public void render(ShaderProgram shaderProgram, Matrix4 viewWorldMatrix, int depth, boolean putDataIntoShader, boolean drawWater) {
		// performance!!!
		if (quarterFaces == null) {
			renderSelf(shaderProgram, putDataIntoShader,drawWater);
			return;
		}

		Vector3 viewVector = viewWorldMatrix.times(new Vector4(normal,0)).extractVector3();//.normalize();
		Vector3 distanceVector = viewWorldMatrix.times(new Vector4(normal, 1)).extractVector3();
		
		double factor = viewVector.dot(cameraDirection);
		if (factor < 0) {
			factor = 0;
		}

		// effect extremification and normalization
		factor = factor /** factor*/ / viewVector.lengthSquared();
		
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
					facePart.render(shaderProgram, viewWorldMatrix, depth, putDataIntoShader, drawWater);
				}
			}
		} else {
			renderSelf(shaderProgram, putDataIntoShader, drawWater);
		}
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public Tile getTile() {
		return tile;
	}
	public void setTile(Tile tile) {
		this.tile = tile;
	}

	public Vector3 getCorner1() {
		return corner1;
	}
	public Vector3 getCorner2() {
		return corner2;
	}
	public Vector3 getCorner3() {
		return corner3;
	}

	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}

	public Mesh getWaterMesh() {
		return waterMesh;
	}
	public void setWaterMesh(Mesh waterMesh) {
		this.waterMesh = waterMesh;
	}

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
