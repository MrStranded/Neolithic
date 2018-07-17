package math;

import engine.math.numericalObjects.Matrix3;
import engine.math.numericalObjects.Vector3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Matrix3Test {

	@Test
	public void testIdentityMatrix() {

		Matrix3 m = new Matrix3();

		assertEquals(1,m.getA11());
		assertEquals(1,m.getA22());
		assertEquals(1,m.getA33());

		assertEquals(0,m.getA12());
		assertEquals(0,m.getA13());
		assertEquals(0,m.getA21());
		assertEquals(0,m.getA23());
		assertEquals(0,m.getA31());
		assertEquals(0,m.getA32());
	}

	@Test
	public void testScalarMultiplication() {

		Matrix3 m = new Matrix3(1, 2, 3, -1, 1, 2, 5, 5, 5);

		m = m.times(2);

		assertEquals(2,m.getA11());
		assertEquals(4,m.getA12());
		assertEquals(6,m.getA13());
		assertEquals(-2,m.getA21());
		assertEquals(2,m.getA22());
		assertEquals(4,m.getA23());
		assertEquals(10,m.getA31());
		assertEquals(10,m.getA32());
		assertEquals(10,m.getA33());
	}

	@Test
	public void testVectorMultiplication() {

		Matrix3 i = new Matrix3();

		Vector3 a = new Vector3(1, 2, 3);

		Vector3 b = i.times(a);

		assertEquals(1, b.getX());
		assertEquals(2, b.getY());
		assertEquals(3, b.getZ());

		Matrix3 m = new Matrix3(1, 2, 3, 0, 1, 2, 5, 5, 5);
		Vector3 c = m.times(a); // x = 1 + 4 + 9 = 14.   y = 0 + 2 + 6 = 8.   z = 5 + 10 + 15 = 30.

		assertEquals(14, c.getX());
		assertEquals(8, c.getY());
		assertEquals(30, c.getZ());
	}

	@Test
	public void testMatrixMulitplication() {

		Matrix3 m = new Matrix3(1, 2, 3, 0, 1, 2, 5, 5, 5);
		Matrix3 n = new Matrix3(0,3,-1,2,1,0,7,-2,3);

		Matrix3 o = n.times(m);

		assertEquals(-5,o.getA11());
		assertEquals(-2,o.getA12());
		assertEquals(1,o.getA13());
		assertEquals(2,o.getA21());
		assertEquals(5,o.getA22());
		assertEquals(8,o.getA23());
		assertEquals(22,o.getA31());
		assertEquals(27,o.getA32());
		assertEquals(32,o.getA33());

		Matrix3 p = m.times(n);

		assertEquals(25,p.getA11());
		assertEquals(-1,p.getA12());
		assertEquals(8,p.getA13());
		assertEquals(16,p.getA21());
		assertEquals(-3,p.getA22());
		assertEquals(6,p.getA23());
		assertEquals(45,p.getA31());
		assertEquals(10,p.getA32());
		assertEquals(10,p.getA33());
	}
}
