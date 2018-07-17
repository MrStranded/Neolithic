package engine.utils;

import math.Vector2;
import math.Vector3;

public class VectorConverter {

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
