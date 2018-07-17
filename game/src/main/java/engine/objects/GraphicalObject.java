package engine.objects;

import engine.data.Texture;
import engine.objects.movement.MoveableObject;
import math.Matrix4;
import math.Vector3;
import engine.data.Mesh;

public class GraphicalObject extends MoveableObject {

	private Mesh mesh;
	private boolean useDepthTest = true;

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

	public Matrix4 getWorldMatrix() {
		return matrix;
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

	public boolean isUseDepthTest() {
		return useDepthTest;
	}
	public void setUseDepthTest(boolean useDepthTest) {
		this.useDepthTest = useDepthTest;
	}
}
