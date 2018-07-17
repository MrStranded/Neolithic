package load;

import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import engine.data.Mesh;
import engine.data.MeshFace;
import math.Vector2;
import math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class OBJLoader {

	public static Mesh loadMesh(String fileName) throws Exception {

		List<String> lines = StringLoader.readAllLines(fileName);

		List<Vector3> vertices = new ArrayList<>();
		List<Vector2> textures = new ArrayList<>();
		List<Vector3> normals = new ArrayList<>();
		List<MeshFace> faces = new ArrayList<>();

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
					vertices.add(v);
					break;

				case "vt": // texture coordinate
					Vector2 vt = new Vector2(
							Float.parseFloat(tokens[1]),
							Float.parseFloat(tokens[2])
					);
					textures.add(vt);
					break;

				case "vn": // normal
					Vector3 vn = new Vector3(
							Float.parseFloat(tokens[1]),
							Float.parseFloat(tokens[2]),
							Float.parseFloat(tokens[3])
					);
					normals.add(vn);
					break;

				case "f": // face
					MeshFace f = new MeshFace();
					f.putOBJData(
							tokens[1],
							tokens[2],
							tokens[3]
					);
					faces.add(f);
					break;

				default: // ignore other information
					break;
			}
		}

		return null;
	}
}
