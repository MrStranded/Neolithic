package engine.utils;

import math.Vector3;

public class VectorConverter {

	public static float[] VectorArrayToFloatArray(Vector3[] vectors) {

		float[] floats = new float[vectors.length*3];

		for (int i=0; i<vectors.length; i++) {
			int j = i*3;
			floats[j+0] = (float) vectors[i].getX();
			floats[j+1] = (float) vectors[i].getY();
			floats[j+2] = (float) vectors[i].getZ();
		}

		return floats;
	}
}
