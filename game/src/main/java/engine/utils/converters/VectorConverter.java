package engine.utils.converters;

import engine.math.numericalObjects.Vector2;
import engine.math.numericalObjects.Vector3;

import java.util.List;

public class VectorConverter {

	public static float[] Vector3ListToFloatArray(List<Vector3> vectors) {
		float[] floats = new float[vectors.size()*3];

		int i=0;
		for (Vector3 vector: vectors) {
			floats[i++] = (float) vector.getX();
			floats[i++] = (float) vector.getY();
			floats[i++] = (float) vector.getZ();
		}

		return floats;
	}

	public static float[] Vector2ListToFloatArray(List<Vector2> vectors) {
		float[] floats = new float[vectors.size()*2];

		int i=0;
		for (Vector2 vector: vectors) {
			floats[i++] = (float) vector.getX();
			floats[i++] = (float) vector.getY();
		}

		return floats;
	}

	public static float[] Vector3ArrayToFloatArray(Vector3[] vectors) {
		float[] floats = new float[vectors.length*3];

		for (int i=0; i<vectors.length; i++) {
			int j = i*3;
			floats[j+0] = (float) vectors[i].getX();
			floats[j+1] = (float) vectors[i].getY();
			floats[j+2] = (float) vectors[i].getZ();
		}

		return floats;
	}

	public static float[] Vector2ArrayToFloatArray(Vector2[] vectors) {
		float[] floats = new float[vectors.length*2];

		for (int i=0; i<vectors.length; i++) {
			if (vectors[i] != null) {
				int j = i * 2;
				floats[j + 0] = (float) vectors[i].getX();
				floats[j + 1] = (float) vectors[i].getY();
			}
		}

		return floats;
	}
}
