package engine.utils.converters;

import java.util.List;

public class IntegerConverter {

	public static int[] IntegerListToIntArray(List<Integer> integerList) {

		int[] intArray = new int[integerList.size()];
		for (int i=0; i<intArray.length; i++) {
			intArray[i] = integerList.get(i);
		}

		return intArray;
	}
}
