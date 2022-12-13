package tests.math;

import engine.math.numericalObjects.Vector3;
import engine.parser.utils.Logger;

public class VectorPerformanceTest {

	public static void main(String[] args) {

		// testing out inplace multiplication

		int m = 100;
		int n = 1000000;
		long t, tn=0, ti=0;

		Logger.raw("performing " + m + "x"+ n + " multiplications on a vector3");

		for (int j=0; j<m; j++) {

			Vector3 a = new Vector3(1,2,3);
			Vector3 b = new Vector3(3,2,1);

			t = System.nanoTime();
			for (int i = 0; i < n; i++) {
				a = a.cross(b);
			}
			tn += System.nanoTime() - t;

			t = System.nanoTime();
			for (int i = 0; i < n; i++) {
				a = a.crossInplace(b);
			}
			ti += System.nanoTime() - t;
		}

		Logger.raw("normal took " + tn + " nanoseconds");
		Logger.raw("inplace took "+ti+" nanoseconds");
		Logger.raw("inplace is "+((double) tn/(double) ti)+" times faster");
	}

}
