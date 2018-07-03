package math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Matrix4Test {

	@Test
	public void testIdentityMatrix() {

		Matrix4 m = new Matrix4();

		assertEquals(1,m.getA11());
		assertEquals(1,m.getA22());
		assertEquals(1,m.getA33());
		assertEquals(1,m.getA44());

		assertEquals(0,m.getA12());
		assertEquals(0,m.getA13());
		assertEquals(0,m.getA14());
		assertEquals(0,m.getA21());
		assertEquals(0,m.getA23());
		assertEquals(0,m.getA24());
		assertEquals(0,m.getA31());
		assertEquals(0,m.getA32());
		assertEquals(0,m.getA34());
		assertEquals(0,m.getA41());
		assertEquals(0,m.getA42());
		assertEquals(0,m.getA43());
	}

	@Test
	public void testScalarMultiplication() {

		Matrix4 m = new Matrix4(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);

		m = m.times(2);

		assertEquals(2,m.getA11());
		assertEquals(4,m.getA12());
		assertEquals(6,m.getA13());
		assertEquals(8,m.getA14());
		assertEquals(10,m.getA21());
		assertEquals(12,m.getA22());
		assertEquals(14,m.getA23());
		assertEquals(16,m.getA24());
		assertEquals(18,m.getA31());
		assertEquals(20,m.getA32());
		assertEquals(22,m.getA33());
		assertEquals(24,m.getA34());
		assertEquals(26,m.getA41());
		assertEquals(28,m.getA42());
		assertEquals(30,m.getA43());
		assertEquals(32,m.getA44());
	}

	@Test
	public void testVectorMultiplication() {

		Matrix4 i = new Matrix4();

		Vector4 a = new Vector4(1, 2, 3, 4);

		Vector4 b = i.times(a);

		assertEquals(1, b.getX());
		assertEquals(2, b.getY());
		assertEquals(3, b.getZ());
		assertEquals(4, b.getW());

		Matrix4 m = new Matrix4(1,2,3,4,4,3,2,1,7,2,7,2,3,4,5,6);
		Vector4 c = m.times(a);

		assertEquals(30, c.getX());
		assertEquals(20, c.getY());
		assertEquals(40, c.getZ());
		assertEquals(50, c.getW());
	}

	@Test
	public void testMatrixMulitplication() {

		Matrix4 m = new Matrix4(1,2,3,4,4,3,2,1,7,2,7,2,3,4,5,6);
		Matrix4 n = new Matrix4(-1,2,-3,-4,2,3,2,3,5,3,1,8,6,1,2,3);

		Matrix4 o = m.times(n);

		assertEquals(42,o.getA11());
		assertEquals(21,o.getA12());
		assertEquals(12,o.getA13());
		assertEquals(38,o.getA14());
		assertEquals(18,o.getA21());
		assertEquals(24,o.getA22());
		assertEquals(-2,o.getA23());
		assertEquals(12,o.getA24());
		assertEquals(44,o.getA31());
		assertEquals(43,o.getA32());
		assertEquals(-6,o.getA33());
		assertEquals(40,o.getA34());
		assertEquals(66,o.getA41());
		assertEquals(39,o.getA42());
		assertEquals(16,o.getA43());
		assertEquals(58,o.getA44());
	}

	@Test
	public void testExtraction() {

		Matrix4 m = new Matrix4(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16);

		Matrix3 n = m.extractMatrix3();

		assertEquals(1,n.getA11());
		assertEquals(2,n.getA12());
		assertEquals(3,n.getA13());
		assertEquals(5,n.getA21());
		assertEquals(6,n.getA22());
		assertEquals(7,n.getA23());
		assertEquals(9,n.getA31());
		assertEquals(10,n.getA32());
		assertEquals(11,n.getA33());
	}
}
