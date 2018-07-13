package renderer.color;

import engine.renderer.color.RGBA;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RGBATest {

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Addition

	@Test
	public void testAddition() {

		RGBA c1 = new RGBA(0.125, 0.25, 0.375, 0.5);
		RGBA c2 = new RGBA(0.25,-0.125,0.125,0.5);

		RGBA c3 = c1.plus(c2);

		assertEquals(0.375, c3.getR());
		assertEquals(0.125, c3.getG());
		assertEquals(0.5, c3.getB());
		assertEquals(1, c3.getA());
	}

	@Test
	public void testAdditionInplace() {

		RGBA c1 = new RGBA(0.125, 0.25, 0.375, 0.5);
		RGBA c2 = new RGBA(0.25,-0.125,0.125,0.5);

		c1.plusInplace(c2);

		assertEquals(0.375, c1.getR());
		assertEquals(0.125, c1.getG());
		assertEquals(0.5, c1.getB());
		assertEquals(1, c1.getA());
	}

	@Test
	public void testAdditionNoAlpha() {

		RGBA c1 = new RGBA(0.125, 0.25, 0.375, 0.5);
		RGBA c2 = new RGBA(0.25,-0.125,0.125,0.25);

		RGBA c3 = c1.plusNoAlpha(c2);

		assertEquals(0.375, c3.getR());
		assertEquals(0.125, c3.getG());
		assertEquals(0.5, c3.getB());
		assertEquals(0.5, c3.getA());
	}

	@Test
	public void testAdditionNoAlphaInplace() {

		RGBA c1 = new RGBA(0.125, 0.25, 0.375, 0.5);
		RGBA c2 = new RGBA(0.25,-0.125,0.125,0.25);

		c1.plusNoAlphaInplace(c2);

		assertEquals(0.375, c1.getR());
		assertEquals(0.125, c1.getG());
		assertEquals(0.5, c1.getB());
		assertEquals(0.5, c1.getA());
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Subtraction

	@Test
	public void testSubtraction() {

		RGBA c1 = new RGBA(0.125, 0.25, 0.375, 0.5);
		RGBA c2 = new RGBA(0.25,-0.125,0.125,0.5);

		RGBA c3 = c1.minus(c2);

		assertEquals(-0.125, c3.getR());
		assertEquals(0.375, c3.getG());
		assertEquals(0.25, c3.getB());
		assertEquals(0, c3.getA());
	}

	@Test
	public void testSubtractionInplace() {

		RGBA c1 = new RGBA(0.125, 0.25, 0.375, 0.5);
		RGBA c2 = new RGBA(0.25,-0.125,0.125,0.5);

		c1.minusInplace(c2);

		assertEquals(-0.125, c1.getR());
		assertEquals(0.375, c1.getG());
		assertEquals(0.25, c1.getB());
		assertEquals(0, c1.getA());
	}

	@Test
	public void testSubtractionNoAlpha() {

		RGBA c1 = new RGBA(0.125, 0.25, 0.375, 0.5);
		RGBA c2 = new RGBA(0.25,-0.125,0.125,0.25);

		RGBA c3 = c1.minusNoAlpha(c2);

		assertEquals(-0.125, c3.getR());
		assertEquals(0.375, c3.getG());
		assertEquals(0.25, c3.getB());
		assertEquals(0.5, c3.getA());
	}

	@Test
	public void testSubtractionNoAlphaInplace() {

		RGBA c1 = new RGBA(0.125, 0.25, 0.375, 0.5);
		RGBA c2 = new RGBA(0.25,-0.125,0.125,0.25);

		c1.minusNoAlphaInplace(c2);

		assertEquals(-0.125, c1.getR());
		assertEquals(0.375, c1.getG());
		assertEquals(0.25, c1.getB());
		assertEquals(0.5, c1.getA());
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Multiplication

	@Test
	public void testMultiplication() {

		RGBA c1 = new RGBA(0.125, 0.25, 0.375, 0.5);
		RGBA c2 = c1.times(2);

		assertEquals(0.25, c2.getR());
		assertEquals(0.5, c2.getG());
		assertEquals(0.75, c2.getB());
		assertEquals(1, c2.getA());
	}

	@Test
	public void testMultiplicationInplace() {

		RGBA c1 = new RGBA(0.125, 0.25, 0.375, 0.5);
		c1.timesInplace(2);

		assertEquals(0.25,c1.getR());
		assertEquals(0.5,c1.getG());
		assertEquals(0.75,c1.getB());
		assertEquals(1,c1.getA());
	}

	@Test
	public void testMultiplicationNoAlpha() {

		RGBA c1 = new RGBA(0.125, 0.25, 0.375, 0.5);
		RGBA c2 = c1.timesNoAlpha(2);

		assertEquals(0.25, c2.getR());
		assertEquals(0.5, c2.getG());
		assertEquals(0.75, c2.getB());
		assertEquals(0.5, c2.getA());
	}

	@Test
	public void testMultiplicationNoAlphaInplace() {

		RGBA c1 = new RGBA(0.125, 0.25, 0.375, 0.5);
		c1.timesNoAplhaInplace(2);

		assertEquals(0.25,c1.getR());
		assertEquals(0.5,c1.getG());
		assertEquals(0.75,c1.getB());
		assertEquals(0.5,c1.getA());
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Utility

	@Test
	public void testClamping() {

		RGBA c1 = new RGBA(-1,2,-3,4);
		c1.clamp();

		assertEquals(0,c1.getR());
		assertEquals(1,c1.getG());
		assertEquals(0,c1.getB());
		assertEquals(1,c1.getA());

		RGBA c2 = new RGBA(10,-2,3,-4);
		c2.clamp();

		assertEquals(1,c2.getR());
		assertEquals(0,c2.getG());
		assertEquals(1,c2.getB());
		assertEquals(0,c2.getA());
	}
}
