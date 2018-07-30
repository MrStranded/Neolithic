package engine.graphics.objects;

import constants.GraphicalConstants;
import engine.graphics.objects.textures.Texture;
import engine.graphics.objects.movement.MoveableObject;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.math.numericalObjects.Vector3;
import engine.graphics.objects.models.Mesh;

public class GraphicalObject extends MoveableObject {

	protected CompositeMesh compositeMesh;
	protected boolean useDepthTest = true;
	protected boolean affectedByLight = true;
	protected boolean isStatic = false; // static objects won't be moved around and are always in the same place around the camera

	public GraphicalObject(CompositeMesh compositeMesh) {
		this.compositeMesh = compositeMesh;
	}
	public GraphicalObject(Mesh mesh) {
		compositeMesh = new CompositeMesh(mesh);
	}
	public GraphicalObject() {}

	// ###################################################################################
	// ################################ Rendering ########################################
	// ###################################################################################

	public void render(ShaderProgram shaderProgram, boolean sendMaterial) {
		compositeMesh.render(shaderProgram, sendMaterial, useDepthTest);
	}

	// ###################################################################################
	// ################################ Clean Up #########################################
	// ###################################################################################

	public void cleanUp() {
		compositeMesh.cleanUp();
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public CompositeMesh getCompositeMesh() {
		return compositeMesh;
	}

	public Mesh getMesh() {
		return compositeMesh.getMesh();
	}
	public void setMesh(Mesh mesh) {
		compositeMesh = new CompositeMesh(mesh);
	}

	public Vector3 getPosition() {
		return position;
	}

	public Vector3 getScale() {
		return scale;
	}

	public Vector3 getRotation() {
		return rotation;
	}

	public boolean usesDepthTest() {
		return useDepthTest;
	}
	public void setUseDepthTest(boolean useDepthTest) {
		this.useDepthTest = useDepthTest;
	}

	public boolean isAffectedByLight() {
		return affectedByLight;
	}
	public void setAffectedByLight(boolean affectedByLight) {
		this.affectedByLight = affectedByLight;
	}

	public boolean isStatic() {
		return isStatic;
	}
	public void setStatic(boolean aStatic) {
		isStatic = aStatic;
	}
}
