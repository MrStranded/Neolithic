package load;

import engine.graphics.objects.models.IndexGroup;
import engine.graphics.objects.models.Mesh;
import engine.graphics.objects.models.MeshFace;
import engine.utils.converters.IntegerConverter;
import engine.utils.converters.VectorConverter;
import engine.math.numericalObjects.Vector2;
import engine.math.numericalObjects.Vector3;

import java.util.ArrayList;
import java.util.List;

public class OBJLoader {

	public static Mesh loadMesh(String fileName) throws Exception {
		System.out.println("loading mesh " + fileName);

		List<String> lines = StringLoader.readAllLines(fileName);

		List<Vector3> verticesList = new ArrayList<>();
		List<Vector2> texturesList = new ArrayList<>();
		List<Vector3> normalsList = new ArrayList<>();
		List<MeshFace> facesList = new ArrayList<>();

		// ---------------------- parsing the file
		for (String line : lines) {

			String[] tokens = line.split(" ");

			switch (tokens[0]) {
				case "v": // vertex
					Vector3 v = new Vector3(
							Float.parseFloat(tokens[1]),
							Float.parseFloat(tokens[2]),
							Float.parseFloat(tokens[3])
					);
					verticesList.add(v);
					break;

				case "vt": // texture coordinate
					// texture coordinates are in UV format, so y coordinate has to be flipped
					Vector2 vt = new Vector2(
							Float.parseFloat(tokens[1]),
							1f - Float.parseFloat(tokens[2])
					);
					texturesList.add(vt);
					break;

				case "vn": // normal
					Vector3 vn = new Vector3(
							Float.parseFloat(tokens[1]),
							Float.parseFloat(tokens[2]),
							Float.parseFloat(tokens[3])
					);
					normalsList.add(vn);
					break;

				case "f": // face
					// Attention: no support for faces with more than three vertices!
					MeshFace f = new MeshFace();
					f.putOBJData(
							tokens[1],
							tokens[2],
							tokens[3]
					);
					facesList.add(f);
					break;

				default: // ignore other information
					break;
			}
		}

		// ---------------------- putting data into arrays
		Vector3[] verticesArray = verticesList.toArray(new Vector3[verticesList.size()]);

		int length = verticesArray.length;
		Vector2[] texturesArray = new Vector2[length];
		Vector3[] normalsArray = new Vector3[length];

		List<Integer> indicesList = new ArrayList<>();

		for (MeshFace face : facesList) {
			for (int i=0; i<3; i++) {
				// get current index group of corner of face
				IndexGroup indexGroup = face.getIndexGroups()[i];

				// put the index into the mesh defining vertex index list
				indicesList.add(indexGroup.getPositionIndex());

				// put the corresponding texture coordinates and normals in the place of the vertex index
				if (indexGroup.getTextureCoordinatesIndex() != IndexGroup.UNDEF) {
					texturesArray[indexGroup.getPositionIndex()] = texturesList.get(indexGroup.getTextureCoordinatesIndex());
				}
				normalsArray[indexGroup.getPositionIndex()] = normalsList.get(indexGroup.getNormalIndex());
			}
		}

		// ---------------------- creating color (white)
		float[] colors = new float[verticesArray.length * 4];
		for (int i=0; i<colors.length; i++) {
			colors[i] = 1f;
		}

		// ---------------------- creating mesh
		return new Mesh(
                IntegerConverter.IntegerListToIntArray(indicesList), VectorConverter.Vector3ArrayToFloatArray(verticesArray),
                VectorConverter.Vector3ArrayToFloatArray(normalsArray),
				VectorConverter.Vector2ArrayToFloatArray(texturesArray),
				colors
		);
	}
}
