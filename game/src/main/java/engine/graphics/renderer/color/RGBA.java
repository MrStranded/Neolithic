package engine.graphics.renderer.color;

import java.awt.*;

public class RGBA {

	private double r=0,g=0,b=0,a=1;

	public RGBA() {}

	public RGBA(double r,double g,double b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public RGBA(double r,double g,double b,double a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	// ###################################################################################
	// ################################ Colors ###########################################
	// ###################################################################################

	public static final RGBA WHITE	= new RGBA(1,1,1,1);
	public static final RGBA BLACK	= new RGBA(0,0,0,1);
	public static final RGBA RED	= new RGBA(1,0,0,1);
	public static final RGBA GREEN	= new RGBA(0,1,0,1);
	public static final RGBA BLUE	= new RGBA(0,0,1,1);

	// ###################################################################################
	// ################################ Math #############################################
	// ###################################################################################

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Addition

	public RGBA plus(RGBA other) {
		return new RGBA(r+other.r,g+other.g,b+other.b,a+other.a);
	}
	public RGBA plusInplace(RGBA other) {
		r += other.r;
		g += other.g;
		b += other.b;
		a += other.a;
		return this;
	}

	/**
	 * Note that the alpha value is not changed.
	 * @param other Color
	 * @return new RGBA color with added values
	 */
	public RGBA plusNoAlpha(RGBA other) {
		return new RGBA(r+other.r,g+other.g,b+other.b,a);
	}
	/**
	 * Note that the alpha value is not changed.
	 * @param other Color
	 * @return modified RGBA color with added values
	 */
	public RGBA plusNoAlphaInplace(RGBA other) {
		r += other.r;
		g += other.g;
		b += other.b;
		return this;
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Subtraction

	public RGBA minus(RGBA other) {
		return new RGBA(r-other.r,g-other.g,b-other.b,a-other.a);
	}
	public RGBA minusInplace(RGBA other) {
		r -= other.r;
		g -= other.g;
		b -= other.b;
		a -= other.a;
		return this;
	}

	/**
	 * Note that the alpha value is not changed.
	 * @param other Color
	 * @return new RGBA color with subtracted values
	 */
	public RGBA minusNoAlpha(RGBA other) {
		return new RGBA(r-other.r,g-other.g,b-other.b,a);
	}
	/**
	 * Note that the alpha value is not changed.
	 * @param other Color
	 * @return modified RGBA color with subtracted values
	 */
	public RGBA minusNoAlphaInplace(RGBA other) {
		r -= other.r;
		g -= other.g;
		b -= other.b;
		return this;
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Multiplication

	public RGBA times(double t) {
		return new RGBA(r*t,g*t,b*t,a);
	}
	public RGBA timesInplace(double t) {
		r *= t;
		g *= t;
		b *= t;
		return this;
	}

	/**
	 * Note that the alpha value is not multiplied with t.
	 * @param t factor
	 * @return new RGBA color with multiplied values
	 */
	public RGBA timesNoAlpha(double t) {
		return new RGBA(r*t,g*t,b*t,a);
	}

	/**
	 * Note that the alpha value is not multiplied with t.
	 * @param t factor
	 * @return modified RGBA color with multiplied values
	 */
	public RGBA timesNoAplhaInplace(double t) {
		r *= t;
		g *= t;
		b *= t;
		return this;
	}

	// ###################################################################################
	// ################################ Utility ##########################################
	// ###################################################################################

	/**
	 * Ensures that the color values lie between 0 and 1.
	 * Values above 1 or below 0 get 'clamped'.
	 */
	public void clamp() {
		if (r < 0) { r = 0; }
		if (g < 0) { g = 0; }
		if (b < 0) { b = 0; }
		if (a < 0) { a = 0; }
		if (r > 1) { r = 1; }
		if (g > 1) { g = 1; }
		if (b > 1) { b = 1; }
		if (a > 1) { a = 1; }
	}

	/**
	 * Returns an java.awt.Color with the corresponding values of the RGBA color.
	 * Throws an IllegalArgumentException if any of the values exceeds the range from 0 to 1.
	 * @return color in the java.awt.Color format
	 */
	public Color toColor() throws IllegalArgumentException {
		return new Color((int) (255d*r),(int) (255d*g),(int) (255d*b),(int) (255d*a));
	}

	// ###################################################################################
	// ################################ Printing #########################################
	// ###################################################################################

	public String toString() {
		return "(r: "+r+" ,g: "+g+" ,b: "+b+" ,a: "+a+")";
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public double getR() {
		return r;
	}
	public void setR(double r) {
		this.r = r;
	}

	public double getG() {
		return g;
	}
	public void setG(double g) {
		this.g = g;
	}

	public double getB() {
		return b;
	}
	public void setB(double b) {
		this.b = b;
	}

	public double getA() {
		return a;
	}
	public void setA(double a) {
		this.a = a;
	}
}
