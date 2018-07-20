package engine.graphics.objects;

import engine.graphics.objects.models.Texture;
import engine.graphics.objects.movement.MoveableObject;
import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;
import engine.graphics.objects.models.Mesh;

public class GraphicalObject extends MoveableObject {

	private Mesh mesh;
	private boolean useDepthTest = true;
	private boolean affectedByLight = true;

	public GraphicalObject(Mesh mesh) {

		this.mesh = mesh;
	}

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
		mesh.cleanUp();
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public void setColor(float r, float g, float b) {
		mesh.setColor(r,g,b);
	}
	public void setColor(float r, float g, float b, float a) {
		mesh.setColor(r,g,b,a);
	}

	public void setTexture(Texture texture) {
		mesh.setTexture(texture);
	}

	public Mesh getMesh() {
		return mesh;
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
}
