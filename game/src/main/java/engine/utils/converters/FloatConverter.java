package engine.utils.converters;

import java.util.List;

public class FloatConverter {

	public static float[] FloatListToFloatArray(List<Float> floatList) {

		float[] floatArray = new float[floatList.size()];
		for (int i=0; i<floatArray.length; i++) {
			floatArray[i] = floatList.get(i);
		}

		return floatArray;
	}
}
