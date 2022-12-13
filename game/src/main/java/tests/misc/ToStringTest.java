package tests.misc;

import engine.graphics.renderer.color.RGBA;
import engine.math.numericalObjects.Matrix3;
import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;
import engine.math.numericalObjects.Vector4;
import engine.parser.utils.Logger;

public class ToStringTest {

	public static void main(String[] args) {

		Logger.raw("RGBA --------------------");
		Logger.raw(new RGBA(0.1,0.2,0.3));

		Logger.raw("Vector3 --------------------");
		Logger.raw(new Vector3(1,2,3));

		Logger.raw("Vector4 --------------------");
		Logger.raw(new Vector4(1,2,3,4));

		Logger.raw("Matrix3 --------------------");
		Matrix3 m3 = new Matrix3(1,2,3,4,5,6,7,8,9);
		Logger.raw(m3);
		Matrix3.setPrintMatrixIndices(false);
		Logger.raw(m3);

		Logger.raw("Matrix4 --------------------");
		Matrix4 m4 = new Matrix4(10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25);
		Logger.raw(m4);
		Matrix4.setPrintMatrixIndices(false);
		Logger.raw(m4);

	}

}
