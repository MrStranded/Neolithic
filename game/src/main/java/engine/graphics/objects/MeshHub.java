package engine.graphics.objects;

import engine.graphics.objects.light.ShadowMap;
import engine.graphics.objects.models.Mesh;
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
	private Mesh mesh;
	private List<GraphicalObject> objects;

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

	public void registerObject(GraphicalObject object) {
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

		mesh.prepareRender();

		for (GraphicalObject object : objects) {
			shaderProgram.setUniform("modelViewMatrix", viewMatrix.times(object.getWorldMatrix()));
			shaderProgram.setUniform("affectedByLight", object.isAffectedByLight() ? 1 : 0);
			shaderProgram.setUniform("dynamic", object.isStatic() ? 0 : 1);

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
		mesh.cleanUp();
	}

}
