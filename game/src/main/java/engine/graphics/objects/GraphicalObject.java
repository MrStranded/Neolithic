package engine.graphics.objects;

import engine.graphics.objects.models.Mesh;
import engine.graphics.objects.movement.MoveableObject;
import engine.graphics.objects.textures.Texture;
import engine.math.numericalObjects.Vector3;
import engine.parser.utils.Logger;
import load.TextureLoader;

public class GraphicalObject extends MoveableObject {

	protected Mesh mesh;
	protected boolean useDepthTest = true;
	protected boolean affectedByLight = true;
	protected boolean affectedByShadow = true;
	protected boolean lightDirectionFlipped = false;
	protected boolean isStatic = false; // static objects won't be moved around and are always in the same place around the camera
	protected boolean renderInScene = true;
	protected boolean renderInShadowMap = true;

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

	public void renderForShadowMap() {
		if (renderInShadowMap) {
			mesh.renderForShadowMap();
		}
	}

	public void renderForGUI() {
		mesh.renderForGUI();
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

	public void setTexture(String path) {
		Texture texture = TextureLoader.loadTexture(path);
		if (texture == null) {
			Logger.error("Could not load texture: " + path);
			return;
		}

		mesh.getMaterial().setTexture(texture);
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

	public boolean isAffectedByShadow() {
		return affectedByShadow;
	}
	public void setAffectedByShadow(boolean affectedByShadow) {
		this.affectedByShadow = affectedByShadow;
	}

	public boolean isLightDirectionFlipped() {
		return lightDirectionFlipped;
	}
	public void setLightDirectionFlipped(boolean lightDirectionFlipped) {
		this.lightDirectionFlipped = lightDirectionFlipped;
	}

	public boolean isStatic() {
		return isStatic;
	}
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	public boolean isRenderInScene() {
		return renderInScene;
	}
	public void setRenderInScene(boolean renderInScene) {
		this.renderInScene = renderInScene;
	}

	public boolean isRenderInShadowMap() {
		return renderInShadowMap;
	}
	public void setRenderInShadowMap(boolean renderInShadowMap) {
		this.renderInShadowMap = renderInShadowMap;
	}
}
