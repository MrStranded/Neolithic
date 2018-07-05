package main.engine.graphics.math;

/**
 * Created by michael1337 on 02/07/18.
 */
public class Matrix3 {

	private final double
			a11,a12,a13,
			a21,a22,a23,
			a31,a32,a33;

	public Matrix3 (double a11,double a12,double a13,
	                double a21,double a22,double a23,
	                double a31,double a32,double a33) {
		this.a11 = a11; this.a12 = a12; this.a13 = a13;
		this.a21 = a21; this.a22 = a22; this.a23 = a23;
		this.a31 = a31; this.a32 = a32; this.a33 = a33;
	}

	public Matrix3 (Matrix3 other) {
		a11 = other.a11; a12 = other.a12; a13 = other.a13;
		a21 = other.a21; a22 = other.a22; a23 = other.a23;
		a31 = other.a31; a32 = other.a32; a33 = other.a33;
	}

	/**
	 * Creates an identity matrix.
	 */
	public Matrix3 () {
		a11 = 1; a12 = 0; a13 = 0;
		a21 = 0; a22 = 1; a23 = 0;
		a31 = 0; a32 = 0; a33 = 1;
	}

	// ###################################################################################
	// ################################ Math #############################################
	// ###################################################################################

	public Matrix3 plus (Matrix3 other) {
		return new Matrix3(
				a11+other.a11,a12+other.a12,a13+other.a13,
				a21+other.a21,a22+other.a22,a23+other.a23,
				a31+other.a31,a32+other.a32,a33+other.a33);
	}

	public Matrix3 minus (Matrix3 other) {
		return new Matrix3(
				a11-other.a11,a12-other.a12,a13-other.a13,
				a21-other.a21,a22-other.a22,a23-other.a23,
				a31-other.a31,a32-other.a32,a33-other.a33);
	}

	public Matrix3 times (double t) {
		return new Matrix3(
				a11*t,a12*t,a13*t,
				a21*t,a22*t,a23*t,
				a31*t,a32*t,a33*t);
	}

	public Matrix3 times (Matrix3 o) {
		return new Matrix3(
				a11*o.a11+a12*o.a21+a13*o.a31, a11*o.a12+a12*o.a22+a13*o.a32, a11*o.a13+a12*o.a23+a13*o.a33,
				a21*o.a11+a22*o.a21+a23*o.a31, a21*o.a12+a22*o.a22+a23*o.a32, a21*o.a13+a22*o.a23+a23*o.a33,
				a31*o.a11+a32*o.a21+a33*o.a31, a31*o.a12+a32*o.a22+a33*o.a32, a31*o.a13+a32*o.a23+a33*o.a33);
	}

	public Vector3 times (Vector3 o) {
		return new Vector3(
				a11*o.getX()+a12*o.getY()+a13*o.getZ(),
				a21*o.getX()+a22*o.getY()+a23*o.getZ(),
				a31*o.getX()+a32*o.getY()+a33*o.getZ());
	}

	// ###################################################################################
	// ################################ Test #############################################
	// ###################################################################################

	public boolean equals (Matrix3 o) {
		return (
				(a11==o.a11)&&(a12==o.a12)&&(a13==o.a13)&&
				(a21==o.a21)&&(a22==o.a22)&&(a23==o.a23)&&
				(a31==o.a31)&&(a32==o.a32)&&(a33==o.a33));
	}

}