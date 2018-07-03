package math;

/**
 * Created by michael1337 on 02/07/18.
 */
public class Matrix4 {

	private final double
			a11,a12,a13,a14,
			a21,a22,a23,a24,
			a31,a32,a33,a34,
			a41,a42,a43,a44;

	public Matrix4(double a11, double a12, double a13, double a14,
	               double a21, double a22, double a23, double a24,
	               double a31, double a32, double a33, double a34,
	               double a41, double a42, double a43, double a44) {
		this.a11 = a11; this.a12 = a12; this.a13 = a13; this.a14 = a14;
		this.a21 = a21; this.a22 = a22; this.a23 = a23; this.a24 = a24;
		this.a31 = a31; this.a32 = a32; this.a33 = a33; this.a34 = a34;
		this.a41 = a41; this.a42 = a42; this.a43 = a43; this.a44 = a44;
	}

	public Matrix4(Matrix4 other) {
		a11 = other.a11; a12 = other.a12; a13 = other.a13; a14 = other.a14;
		a21 = other.a21; a22 = other.a22; a23 = other.a23; a24 = other.a24;
		a31 = other.a31; a32 = other.a32; a33 = other.a33; a34 = other.a34;
		a41 = other.a41; a42 = other.a42; a43 = other.a43; a44 = other.a44;
	}

	/**
	 * Creates an identity matrix.
	 */
	public Matrix4() {
		a11 = 1; a12 = 0; a13 = 0; a14 = 0;
		a21 = 0; a22 = 1; a23 = 0; a24 = 0;
		a31 = 0; a32 = 0; a33 = 1; a34 = 0;
		a41 = 0; a42 = 0; a43 = 0; a44 = 1;
	}

	// ###################################################################################
	// ################################ Math #############################################
	// ###################################################################################

	public Matrix4 plus(Matrix4 other) {
		return new Matrix4(
				a11+other.a11,a12+other.a12,a13+other.a13,a14+other.a14,
				a21+other.a21,a22+other.a22,a23+other.a23,a24+other.a24,
				a31+other.a31,a32+other.a32,a33+other.a33,a34+other.a34,
				a41+other.a41,a42+other.a42,a43+other.a43,a44+other.a44);
	}

	public Matrix4 minus(Matrix4 other) {
		return new Matrix4(
				a11-other.a11,a12-other.a12,a13-other.a13,a14-other.a14,
				a21-other.a21,a22-other.a22,a23-other.a23,a24-other.a24,
				a31-other.a31,a32-other.a32,a33-other.a33,a34-other.a34,
				a41-other.a41,a42-other.a42,a43-other.a43,a44-other.a44);
	}

	public Matrix4 times(double t) {
		return new Matrix4(
				a11*t,a12*t,a13*t,a14*t,
				a21*t,a22*t,a23*t,a24*t,
				a31*t,a32*t,a33*t,a34*t,
				a41*t,a42*t,a43*t,a44*t);
	}

	public Matrix4 times(Matrix4 o) {
		return new Matrix4(
				a11*o.a11+a12*o.a21+a13*o.a31+a14*o.a41, a11*o.a12+a12*o.a22+a13*o.a32+a14*o.a42, a11*o.a13+a12*o.a23+a13*o.a33+a14*o.a43, a11*o.a14+a12*o.a24+a13*o.a34+a14*o.a44,
				a21*o.a11+a22*o.a21+a23*o.a31+a24*o.a41, a21*o.a12+a22*o.a22+a23*o.a32+a24*o.a42, a21*o.a13+a22*o.a23+a23*o.a33+a24*o.a43, a21*o.a14+a22*o.a24+a23*o.a34+a24*o.a44,
				a31*o.a11+a32*o.a21+a33*o.a31+a34*o.a41, a31*o.a12+a32*o.a22+a33*o.a32+a34*o.a42, a31*o.a13+a32*o.a23+a33*o.a33+a34*o.a43, a31*o.a14+a32*o.a24+a33*o.a34+a34*o.a44,
				a41*o.a11+a42*o.a21+a43*o.a31+a44*o.a41, a41*o.a12+a42*o.a22+a43*o.a32+a44*o.a42, a41*o.a13+a42*o.a23+a43*o.a33+a44*o.a43, a41*o.a14+a42*o.a24+a43*o.a34+a44*o.a44);
	}

	public Vector4 times(Vector4 o) {
		return new Vector4(
				a11*o.getX()+a12*o.getY()+a13*o.getZ()+a14*o.getW(),
				a21*o.getX()+a22*o.getY()+a23*o.getZ()+a24*o.getW(),
				a31*o.getX()+a32*o.getY()+a33*o.getZ()+a34*o.getW(),
				a41*o.getX()+a42*o.getY()+a43*o.getZ()+a44*o.getW());
	}

	/**
	 * Cuts out upper left 3x3 fields. Other fields are ignored.
	 * @return Matrix3 with upper left values of this matrix.
	 */
	public Matrix3 extractMatrix3() {
		return new Matrix3(
				a11,a12,a13,
				a21,a22,a23,
				a31,a32,a33);
	}

	// ###################################################################################
	// ################################ Test #############################################
	// ###################################################################################

	public boolean equals(Matrix4 o) {
		return (
				(a11==o.a11)&&(a12==o.a12)&&(a13==o.a13)&&(a14==o.a14)&&
				(a21==o.a21)&&(a22==o.a22)&&(a23==o.a23)&&(a24==o.a24)&&
				(a31==o.a31)&&(a32==o.a32)&&(a33==o.a33)&&(a34==o.a34)&&
				(a41==o.a41)&&(a42==o.a42)&&(a43==o.a43)&&(a44==o.a44));
	}

	// ###################################################################################
	// ################################ Gettes and Setters ###############################
	// ###################################################################################

	public double getA11() {
		return a11;
	}
	public double getA12() {
		return a12;
	}
	public double getA13() {
		return a13;
	}
	public double getA14() {
		return a14;
	}
	public double getA21() {
		return a21;
	}
	public double getA22() {
		return a22;
	}
	public double getA23() {
		return a23;
	}
	public double getA24() {
		return a24;
	}
	public double getA31() {
		return a31;
	}
	public double getA32() {
		return a32;
	}
	public double getA33() {
		return a33;
	}
	public double getA34() {
		return a34;
	}
	public double getA41() {
		return a41;
	}
	public double getA42() {
		return a42;
	}
	public double getA43() {
		return a43;
	}
	public double getA44() {
		return a44;
	}
}
