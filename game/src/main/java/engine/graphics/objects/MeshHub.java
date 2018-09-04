package engine.graphics.objects;

import engine.graphics.objects.light.ShadowMap;
import engine.graphics.objects.models.Mesh;
import engine.graphics.objects.movement.MoveableObject;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.math.numericalObjects.Matrix4;
import engine.parser.Logger;
import load.OBJLoader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A MeshHub object connects a mesh to a list of GraphicalObjects.
 */
public class MeshHub {

	private String meshPath;
	private Mesh mesh = null;
	private List<MoveableObject> objects;

	public MeshHub(String meshPath) {
		this.meshPath = meshPath;
		objects = new ArrayList<>(16);
	}

	public void loadMesh() {
		try {
			mesh = OBJLoader.loadMesh(meshPath);
		} catch (Exception e) {
			Logger.error("Could not load mesh: '" + meshPath + "'");
			e.printStackTrace();
		}
	}

	public void registerObject(MoveableObject object) {
		objects.add(object);
	}

	/**
	 * This method clears the list of registered objects.
	 */
	public void clear() {
		objects.clear();
	}

	public void render(ShaderProgram shaderProgram, Matrix4 viewMatrix, ShadowMap shadowMap) {
		shaderProgram.setUniform("color", mesh.getColor());
		shaderProgram.setUniform("material", mesh.getMaterial());
		shaderProgram.setUniform("affectedByLight", 1);
		shaderProgram.setUniform("dynamic", 1);

		mesh.prepareRender();

		for (MoveableObject object : objects) {
			shaderProgram.setUniform("modelViewMatrix", viewMatrix.times(object.getWorldMatrix()));

			if (shadowMap != null) {
				shaderProgram.setUniform("modelLightViewMatrix", shadowMap.getViewMatrix().times(object.getWorldMatrix()));
			}

			mesh.pureRender();
		}

		mesh.postRender();
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

}
