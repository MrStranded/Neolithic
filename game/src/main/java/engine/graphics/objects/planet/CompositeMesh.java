package engine.graphics.objects.planet;

import constants.GraphicalConstants;
import engine.graphics.objects.models.Mesh;
import engine.graphics.renderer.shaders.ShaderProgram;

public class CompositeMesh {

	private CompositeMesh[] subMeshes;
	private Mesh mesh;

	public CompositeMesh(Mesh mesh, CompositeMesh[] subMeshes) {
		this.mesh = mesh;
		this.subMeshes = subMeshes;
	}
	public CompositeMesh(Mesh mesh) {
		this.mesh = mesh;
	}
	public CompositeMesh(int size) {
		this.subMeshes = new CompositeMesh[size];
	}
	public CompositeMesh() {
	}

	// ###################################################################################
	// ################################ Rendering ########################################
	// ###################################################################################

	/**
	 * Renders all components that are hanged onto this composite mesh.
	 * @param useDepthTest whether the rendering should use depth test
	 */
	public void render(ShaderProgram shaderProgram, boolean sendMaterial, boolean useDepthTest) {
		if (mesh != null) {
			mesh.render(useDepthTest);
		}
		if (subMeshes != null) {
			for (CompositeMesh compositeMesh : subMeshes) {
				if (compositeMesh != null) {
					compositeMesh.render(shaderProgram, sendMaterial, useDepthTest);
				}
			}
		}
	}

	// ###################################################################################
	// ################################ Modification #####################################
	// ###################################################################################

	public void addSubMesh(Mesh mesh) {
		if (subMeshes == null) { // there is no list yet -> create one
			subMeshes = new CompositeMesh[GraphicalConstants.DEFAULT_COMPOSITE_MESH_SIZE];
			subMeshes[0] = new CompositeMesh(mesh);

		} else { // there is a list. but is there space?
			boolean foundSpace = false;

			for (int i=0; i<subMeshes.length; i++) {
				if (subMeshes[i] == null) { // there is space
					subMeshes[i] = new CompositeMesh(mesh);
					foundSpace = true;
					break;
				}
			}

			if (!foundSpace) { // there is no space -> copy array into bigger array and put mesh into that one
				int position = subMeshes.length;

				enlarge();

				subMeshes[position] = new CompositeMesh(mesh);
			}
		}
	}

	/**
	 * Enlarges subMeshes array and copies old values into new array.
	 */
	private void enlarge() {
		int newSize = subMeshes.length + GraphicalConstants.COMPOSITE_MESH_SIZE_CHANGE;
		CompositeMesh[] newSubMeshes = new CompositeMesh[newSize];

		for (int i=0; i<subMeshes.length; i++) {
			newSubMeshes[i] = subMeshes[i];
		}

		subMeshes = newSubMeshes;
	}

	// ###################################################################################
	// ################################ Clean Up #########################################
	// ###################################################################################

	public void cleanUp() {
		if (mesh != null) {
			mesh.cleanUp();
		}

		if (subMeshes != null) {
			for (CompositeMesh compositeMesh : subMeshes) {
				if (compositeMesh != null) {
					compositeMesh.cleanUp();
				}
			}
		}
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public CompositeMesh[] getSubMeshes() {
		return subMeshes;
	}
	public void setSubMeshes(CompositeMesh[] subMeshes) {
		this.subMeshes = subMeshes;
	}

	public void setSubMesh(CompositeMesh compositeMesh, int position) {
		if (subMeshes == null) {
			subMeshes = new CompositeMesh[GraphicalConstants.DEFAULT_COMPOSITE_MESH_SIZE];
			setSubMesh(compositeMesh, position);
			return;
		}

		if (position > subMeshes.length) {
			enlarge();
			setSubMesh(compositeMesh, position);
			return;
		}

		if (position >= 0) {
			subMeshes[position] = compositeMesh;
		}
	}

	public CompositeMesh get(int i) {
		if ((subMeshes != null) && (subMeshes.length > i)) {
			return subMeshes[i];
		}
		return null;
	}

	public Mesh getMesh() {
		return mesh;
	}
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

	public Mesh getDirect(int i) {
		if ((subMeshes != null) && (subMeshes.length > i)) {
			return subMeshes[i].getMesh();
		}
		return null;
	}
}
