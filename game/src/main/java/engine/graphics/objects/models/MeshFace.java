package engine.graphics.objects.models;

public class MeshFace {

	private IndexGroup[] indexGroups;

	public MeshFace() {
		indexGroups = new IndexGroup[3];
	}

	// ###################################################################################
	// ################################ .obj Mesh File Parsing ###########################
	// ###################################################################################

	public void putOBJData(String vertex1, String vertex2, String vertex3) {
		indexGroups[0] = parseOBJLine(vertex1);
		indexGroups[1] = parseOBJLine(vertex2);
		indexGroups[2] = parseOBJLine(vertex3);
	}

	private IndexGroup parseOBJLine(String objData) {

		IndexGroup indexGroup = new IndexGroup();
		String[] indices = objData.split("/");

		// vertex index
		indexGroup.setPositionIndex(Integer.parseInt(indices[0]) - 1); // -1 because indices in .obj files start with 1 (supacrazy)

		// texture coordinates index
		if (indices.length > 1) {

			// this field may be empty, eg. like this: "12//8"
			if (indices[1].length() > 0) {

				indexGroup.setTextureCoordinatesIndex(Integer.parseInt(indices[1]) - 1); // -1 because indices in .obj files start with 1 (supacrazy)
			}
		}

		// normal index
		if (indices.length > 2) {

			indexGroup.setNormalIndex(Integer.parseInt(indices[2]) - 1); // -1 because indices in .obj files start with 1 (supacrazy)
		}

		return indexGroup;
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public IndexGroup[] getIndexGroups() {
		return indexGroups;
	}
}
