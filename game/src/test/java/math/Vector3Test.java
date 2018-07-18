package math;

import engine.math.numericalObjects.Vector3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Please keep in mind: With Vector3 we are dealing with double values.
 * This means that there are real limits to our accuracy.
 * This affects methods such as vectorA.equals(vectorB).
 */

public class Vector3Test {

	private static final double epsilon = 0.00001d;

	@Test
	public void testEquality() {

		Vector3 a = new Vector3(1,2,3);
		Vector3 b = new Vector3(1.0,2.0,3.0);

		assertTrue(a.equals(b));
		assertTrue(b.equals(a));
	}

	@Test
	public void testLength() {

		Vector3 a = new Vector3(1,2,2); // length = 3 = sqrt(1 + 4 + 4)
		assertEquals(3,a.length());

		Vector3 b = new Vector3(3,4,0); // length = 5 = sqrt(9 + 16 + 0)
		assertEquals(5,b.length());
	}

	@Test
	public void testSquaredLength() {

		Vector3 a = new Vector3(5,3,4); // lengthSquared = 50 = 25 + 9 + 16
		assertEquals(50,a.lengthSquared());
	}

	@Test
	public void testAddition() {

		Vector3 a = new Vector3(-10,5,7);
		Vector3 b = new Vector3(100, 3.25, 0);

		Vector3 c = a.plus(b);

		assertEquals(90,c.getX());
		assertEquals(8.25,c.getY());
		assertEquals(7,c.getZ());
	}

	@Test
	public void testAdditionInplace() {

		Vector3 a = new Vector3(-10,5,7);
		Vector3 b = new Vector3(100, 3.25, 0);

		a.plusInplace(b);

		assertEquals(90,a.getX());
		assertEquals(8.25,a.getY());
		assertEquals(7,a.getZ());
	}

	@Test
	public void testSubtraction() {

		Vector3 a = new Vector3(-10,5,7);
		Vector3 b = new Vector3(100, 3.25, 0);

		Vector3 c = a.minus(b);

		assertEquals(-110,c.getX());
		assertEquals(1.75,c.getY());
		assertEquals(7,c.getZ());
	}

	@Test
	public void testSubtractionInplace() {

		Vector3 a = new Vector3(-10,5,7);
		Vector3 b = new Vector3(100, 3.25, 0);

		a.minusInplace(b);

		assertEquals(-110,a.getX());
		assertEquals(1.75,a.getY());
		assertEquals(7,a.getZ());
	}

	@Test
	public void testMultiplication() {

		Vector3 a = new Vector3(0,1,3);

		Vector3 b = a.times(5);

		assertEquals(0,b.getX());
		assertEquals(5,b.getY());
		assertEquals(15,b.getZ());
	}

	@Test
	public void testMultiplicationInplace() {

		Vector3 a = new Vector3(0,1,3);

		a.timesInplace(5);

		assertEquals(0,a.getX());
		assertEquals(5,a.getY());
		assertEquals(15,a.getZ());
	}

	@Test
	public void testDotProduct() {

		Vector3 a = new Vector3(1,2,3);
		Vector3 b = new Vector3(7,0,3);

		assertEquals(16,a.dot(b));
	}

	@Test
	public void testCrossProduct() {

		Vector3 a = new Vector3(1,0,0);
		Vector3 b = new Vector3(0,1,0);

		Vector3 c = a.cross(b);

		assertEquals(0,c.getX());
		assertEquals(0,c.getY());
		assertEquals(1,c.getZ());

		Vector3 d = new Vector3(1,2,3);
		Vector3 e = new Vector3(-1,1,-2);

		Vector3 f = d.cross(e); // x = 2*-2 - 1*3 = -7.   y = 3*-1 - -2*1 = -1.   z = 1*1 - -1*2 = 3

		assertEquals(-7,f.getX());
		assertEquals(-1,f.getY());
		assertEquals(3,f.getZ());
	}

	@Test
	public void testCrossProductInplace() {

		Vector3 a = new Vector3(1,0,0);
		Vector3 b = new Vector3(0,1,0);

		a.crossInplace(b);

		assertEquals(0,a.getX());
		assertEquals(0,a.getY());
		assertEquals(1,a.getZ());

		Vector3 d = new Vector3(1,2,3);
		Vector3 e = new Vector3(-1,1,-2);

		d.crossInplace(e); // x = 2*-2 - 1*3 = -7.   y = 3*-1 - -2*1 = -1.   z = 1*1 - -1*2 = 3

		assertEquals(-7,d.getX());
		assertEquals(-1,d.getY());
		assertEquals(3,d.getZ());
	}

	@Test
	public void testNormalization() {

		Vector3 a = new Vector3(1,2,3);
		Vector3 b = null;

		b = a.normalize();

		assertTrue(Math.abs(b.length() - 1d) < epsilon);

		Vector3 c = new Vector3(-1,0,-100);
		Vector3 d = null;

		d = c.normalize();

		assertTrue(Math.abs(d.length() - 1d) < epsilon);
		
		Vector3 e = new Vector3(0,0,0);

		// normalizing a vector of length zero just returns the same vector
		e = e.normalize();

		assertEquals(0,e.length());
	}

}
