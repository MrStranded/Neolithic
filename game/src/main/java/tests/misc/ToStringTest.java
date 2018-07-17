package tests.misc;

import engine.math.numericalObjects.Matrix3;
import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;
import engine.math.numericalObjects.Vector4;
import engine.graphics.renderer.color.RGBA;

public class ToStringTest {

	public static void main(String[] args) {

		System.out.println("RGBA --------------------");
		System.out.println(new RGBA(0.1,0.2,0.3));

		System.out.println("Vector3 --------------------");
		System.out.println(new Vector3(1,2,3));

		System.out.println("Vector4 --------------------");
		System.out.println(new Vector4(1,2,3,4));

		System.out.println("Matrix3 --------------------");
		Matrix3 m3 = new Matrix3(1,2,3,4,5,6,7,8,9);
		System.out.println(m3);
		Matrix3.setPrintMatrixIndices(false);
		System.out.println(m3);

		System.out.println("Matrix4 --------------------");
		Matrix4 m4 = new Matrix4(10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25);
		System.out.println(m4);
		Matrix4.setPrintMatrixIndices(false);
		System.out.println(m4);

	}

}
