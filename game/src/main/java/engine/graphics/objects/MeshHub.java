package engine.graphics.objects;

import engine.graphics.objects.light.ShadowMap;
import engine.graphics.objects.models.Mesh;
import engine.graphics.objects.movement.MoveableObject;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.math.numericalObjects.Matrix4;
import engine.parser.utils.Logger;
import load.OBJLoader;
import load.PLYLoader;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * A MeshHub object connects a mesh to a list of GraphicalObjects.
 */
public class MeshHub {

	private String meshPath;
	private Mesh mesh = null;
	private double opacity = 1.0;
	private List<Pair<MoveableObject, Boolean>> objectPairs;
	private boolean meshIsLoaded = false;

	public MeshHub(String meshPath) {
		this.meshPath = meshPath;
		objectPairs = new ArrayList<>(16);
	}

	public void loadMesh() {
		try {
			if (meshPath.endsWith(".obj")) {
				mesh = OBJLoader.loadMesh(meshPath);
				meshIsLoaded = true;
			} else if (meshPath.endsWith(".ply")) {
				mesh = PLYLoader.loadMesh(meshPath);
				meshIsLoaded = true;
			} else {
				Logger.error("Mesh format of file '" + meshPath + "' is not supported! Use .obj or .ply files");
			}

			if (opacity < 1.0 && mesh != null) {
				mesh.setAlpha((float) opacity);
			}
		} catch (Exception e) {
			Logger.error("Could not load mesh: '" + meshPath + "'");
			e.printStackTrace();
		}
	}

	public boolean isOpaque() {
		return opacity >= 1;
	}

	public void setMeshOpacity(double opacity) {
		this.opacity = Math.min(1.0, Math.max(0.0, opacity));

		if (mesh == null) {
//			Logger.error("Mesh not yet loaded. Could not set opacity '" + opacity + "' on mesh with path '" + meshPath + "'");
			return;
		}

		mesh.setAlpha((float) opacity);
	}

	public void registerObject(MoveableObject object, boolean selected) {
		objectPairs.add(Pair.of(object, selected));
	}

	/**
	 * This method clears the list of registered objects.
	 */
	public void clear() {
		objectPairs.clear();
	}

	public void render(ShaderProgram shaderProgram, Matrix4 viewMatrix, ShadowMap shadowMap) {
		if (!meshIsLoaded) {
			Logger.error("Mesh with path '" + meshPath + "' is not loaded!");
			return;
		}

		shaderProgram.setUniform("material", mesh.getMaterial());
		shaderProgram.setUniform("affectedByLight", 1);
		shaderProgram.setUniform("dynamic", 1);

		mesh.prepareRender();

		for (Pair<MoveableObject, Boolean> pair : objectPairs) {
			MoveableObject object = pair.getLeft();
			boolean selected = pair.getRight();

			shaderProgram.setUniform("isSelected", selected ? 1 : 0);
			shaderProgram.setUniform("modelViewMatrix", viewMatrix.times(object.getWorldMatrix()));

			if (shadowMap != null) {
				shaderProgram.setUniform("modelLightViewMatrix", shadowMap.getViewMatrix().times(object.getWorldMatrix()));
			}

			mesh.pureRender();
		}

		mesh.postRender();
	}

	public void renderForShadowMap(ShaderProgram shaderProgram, Matrix4 viewMatrix, ShadowMap shadowMap) {
		if (!meshIsLoaded) { return; }

		for (Pair<MoveableObject, Boolean> pair : objectPairs) {
			MoveableObject object = pair.getLeft();

			if (shadowMap != null) {
				shaderProgram.setUniform("modelLightViewMatrix", shadowMap.getViewMatrix().times(object.getWorldMatrix()));
			}

			mesh.renderForShadowMap();
		}
	}

	/**
	 * This method cleans up the mesh data that is held by the meshHub.
	 */
	public void cleanUp() {
		if (mesh != null) {
			mesh.cleanUp();
		}
	}

	public String getMeshPath() {
		return meshPath;
	}

	public boolean isMeshLoaded() {
		return meshIsLoaded;
	}
}
