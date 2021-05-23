package load;

import engine.graphics.objects.models.Mesh;
import engine.graphics.renderer.color.RGBA;
import engine.math.numericalObjects.Vector2;
import engine.math.numericalObjects.Vector3;
import engine.parser.utils.Logger;
import engine.utils.converters.ColorConverter;
import engine.utils.converters.IntegerConverter;
import engine.utils.converters.VectorConverter;

import java.util.ArrayList;
import java.util.List;

public class PLYLoader {

	public static Mesh loadMesh(String fileName) throws Exception {
		Logger.debug("loading mesh " + fileName);

		List<String> lines = StringLoader.readAllLines(fileName);

		int numberOfVertices = 0;
		int numberOfFaces = 0;

		List<Vector3> verticesList = new ArrayList<>();
		List<Vector2> texturesList = new ArrayList<>();
		List<Vector3> normalsList = new ArrayList<>();
		List<RGBA> colorsList = new ArrayList<>();
		List<Integer> indicesList = new ArrayList<>();

		// ---------------------- parsing the header

		int numberOfLines = 0;
		boolean inHeader = true;

		for (String line : lines) {
			String[] tokens = line.split(" ");

			if (inHeader) {

				if (tokens.length > 0) {
					if (numberOfLines == 0 && !tokens[0].equals("ply")) {
						String errorMessage = "Error while parsing .ply file '" + fileName + "': First line of file has to be 'ply'.";
						Logger.error(errorMessage);
						throw new Exception(errorMessage);
					} else {
						if (tokens[0].equals("comment")) {
							// comment - ignore line
						} else if (tokens[0].equals("format")) {
							if (tokens.length < 3 || !tokens[1].equals("ascii") || !tokens[2].equals("1.0")) {
								String errorMessage = "Error while parsing .ply file '" + fileName + "': format has to be 'ascii 1.0'. Other formats are not supported.";
								Logger.error(errorMessage);
								throw new Exception(errorMessage);
							}
						} else if (tokens[0].equals("element")) {
							if (tokens.length >= 3) {
								if (tokens[1].equals("vertex")) {
									numberOfVertices = Integer.parseInt(tokens[2]);
								} else if (tokens[1].equals("face")) {
									numberOfFaces = Integer.parseInt(tokens[2]);
								}
							}
						} else if (tokens[0].equals("end_header")) {
							inHeader = false;
						}
					}
				} else {
					if (numberOfLines == 0) {
						String errorMessage = "Error while parsing .ply file '" + fileName + "': First line of file has to be 'ply'.";
						Logger.error(errorMessage);
						throw new Exception(errorMessage);
					}
				}

			} else {

				if (numberOfVertices > 0) { // we have vertices to read
					// vertex position
					Vector3 v = new Vector3(
							Float.parseFloat(tokens[0]),
							Float.parseFloat(tokens[1]),
							Float.parseFloat(tokens[2])
					);
					verticesList.add(v);

					// texture coordinates - currently not supported
					Vector2 vt = new Vector2(0,0);
					texturesList.add(vt);

					// vertex normal
					Vector3 vn = new Vector3(
							Float.parseFloat(tokens[3]),
							Float.parseFloat(tokens[4]),
							Float.parseFloat(tokens[5])
					);
					normalsList.add(vn);

					// vertex color
					RGBA vc = new RGBA(
							Double.parseDouble(tokens[6]) / 255d,
							Double.parseDouble(tokens[7]) / 255d,
							Double.parseDouble(tokens[8]) / 255d
					);
					colorsList.add(vc);

					numberOfVertices--;

				} else if (numberOfFaces > 0) { // we have faces to read
					int numberOfPoints = Integer.parseInt(tokens[0]);

					// face
					if (numberOfPoints == 3) {
						indicesList.add(Integer.parseInt(tokens[1]));
						indicesList.add(Integer.parseInt(tokens[2]));
						indicesList.add(Integer.parseInt(tokens[3]));
					} else if (numberOfPoints == 4) {
						indicesList.add(Integer.parseInt(tokens[1]));
						indicesList.add(Integer.parseInt(tokens[2]));
						indicesList.add(Integer.parseInt(tokens[3]));
						indicesList.add(Integer.parseInt(tokens[3]));
						indicesList.add(Integer.parseInt(tokens[4]));
						indicesList.add(Integer.parseInt(tokens[1]));
					} else {
						String errorMessage = "Error while parsing .ply file '" + fileName + "': Any other number of points per face other that 3 or 4 are not permitted!";
						Logger.error(errorMessage);
//						throw new Exception(errorMessage);
					}

					numberOfFaces--;
				}

			}

			numberOfLines++;
		}

		// ---------------------- creating mesh
		return new Mesh(
                IntegerConverter.IntegerListToIntArray(indicesList), VectorConverter.Vector3ListToFloatArray(verticesList),
                VectorConverter.Vector2ListToFloatArray(texturesList), ColorConverter.RGBAListToFloatArray(colorsList), VectorConverter.Vector3ListToFloatArray(normalsList)
        );
	}
}
