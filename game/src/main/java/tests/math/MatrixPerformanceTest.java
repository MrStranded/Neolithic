package tests.math;

import engine.math.numericalObjects.Matrix3;
import engine.parser.utils.Logger;

public class MatrixPerformanceTest {

	public static void main(String[] args) {

		// The surprising results of this test were that inplace operations on matrices are about 5 to 10 times slower
		// than the normal ones. Therefore the inplace functionality for matrices has been removed.

		int n = 1000000;
		int k = 100;
		long t,tn=0,ti=0;

		Logger.raw("performing " + k + "x"+ n + " multiplications on a matrix3");

		for (int i=0; i<k; i++) {

			Matrix3 m = new Matrix3(1,2,3,4,5,6,7,8,9);

			t = System.nanoTime();
			for (int j=0; j<n; j++) {
				Matrix3 o = m.times(2);
				Matrix3 p = m.times(0.5);
			}
			tn += System.nanoTime() - t;

			t = System.nanoTime();
			for (int j=0; j<n; j++) {
				// those methods do not exist anymore.
				//m.timesInplace(2);
				//m.timesInplace(0.5);
			}
			ti += System.nanoTime() - t;
		}

		Logger.raw("normal took " + tn + " nanoseconds");
		Logger.raw("inplace took "+ti+" nanoseconds");
		Logger.raw("inplace is "+((double) tn/(double) ti)+" times faster");

	}
}
