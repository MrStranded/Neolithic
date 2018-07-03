package tests.math;

import math.Vector3;

public class VectorPerformanceTest {

	public static void main(String[] args) {

		// testing out inplace multiplication

		int m = 100;
		int n = 1000000;
		long t, tn=0, ti=0;

		System.out.println("performing " + m + "x"+ n + " multiplications on a vector3");

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

		System.out.println("normal took " + tn + " nanoseconds");

		System.out.println("inplace took "+ti+" nanoseconds");

		System.out.println("inplace is "+((double) tn/(double) ti)+" times faster");
	}

}
