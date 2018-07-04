package math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Please keep in mind: With Vector4 we are dealing with double values.
 * This means that there are real limits to our accuracy.
 * This affects methods such as vectorA.equals(vectorB).
 */

public class Vector4Test {

	@Test
	public void testEquality() {

		Vector4 a = new Vector4(1,2,3,4);
		Vector4 b = new Vector4(1.0,2.0,3.0,4.0);

		assertTrue(a.equals(b));
		assertTrue(b.equals(a));
	}

	@Test
	public void testStandardization() {

		Vector4 a = new Vector4(1,3,8,2);

		Vector4 b = a.standardize();

		assertEquals(0.5,b.getX());
		assertEquals(1.5,b.getY());
		assertEquals(4,b.getZ());
		assertEquals(1,b.getW());

		// standardizing a Vector4 with w==0 has to throw an ArtithmeticException
		Vector4 c = new Vector4(1,3,8,0);

		try {
			Vector4 d = c.standardize();

			fail("Method should throw an ArtihmeticException");
		} catch (ArithmeticException e) {
			// all is well. method behaves as expected
		}
	}

	@Test
	public void testStandardizationInplace() {

		Vector4 a = new Vector4(1,3,8,2);

		a.standardizeInplace();

		assertEquals(0.5,a.getX());
		assertEquals(1.5,a.getY());
		assertEquals(4,a.getZ());
		assertEquals(1,a.getW());

		// standardizing a Vector4 with w==0 has to throw an ArtithmeticException
		Vector4 c = new Vector4(1,3,8,0);

		try {
			c.standardizeInplace();

			fail("Method should throw an ArtihmeticException");
		} catch (ArithmeticException e) {
			// all is well. method behaves as expected
		}
	}

	@Test
	public void testExtraction() {

		Vector4 a = new Vector4(100,90,80,70);

		Vector3 b = a.extractVector3();

		assertEquals(100,b.getX());
		assertEquals(90,b.getY());
		assertEquals(80,b.getZ());
	}
}
