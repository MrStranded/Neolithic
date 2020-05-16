package engine.graphics.objects;

import engine.graphics.objects.movement.MoveableObject;
import engine.math.numericalObjects.Vector3;
import engine.graphics.objects.models.Mesh;

public class GraphicalObject extends MoveableObject {

	protected Mesh mesh;
	protected boolean useDepthTest = true;
	protected boolean affectedByLight = true;
	protected boolean isStatic = false; // static objects won't be moved around and are always in the same place around the camera

	public GraphicalObject(Mesh mesh) {
		this.mesh = mesh;
	}
	public GraphicalObject() {}

	// ###################################################################################
	// ################################ Rendering ########################################
	// ###################################################################################

	public void render() {
		mesh.render(useDepthTest);
	}

	// ###################################################################################
	// ################################ Clean Up #########################################
	// ###################################################################################

	public void cleanUp() {
		if (mesh != null) { mesh.cleanUp(); }
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
