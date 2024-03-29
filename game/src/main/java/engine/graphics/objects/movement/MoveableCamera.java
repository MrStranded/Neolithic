package engine.graphics.objects.movement;

import constants.GraphicalConstants;
import engine.math.MatrixCalculations;
import engine.math.Transformations;
import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;

public class MoveableCamera {

	protected double radius = 1d; // distance of camera to center
	protected double yaw = 0d; // rotation around y axis. longitude
	protected double pitch = 0d; // rotation 'around x axis'. lattitude
	protected double tilt = 0d; // tilt of camera. similar to pitch in a way. determines how steeply we look at the planet

	// this matrix holds the complete worldPosition, scale and worldRotation information at once
	protected Matrix4 matrix = new Matrix4();

	// PI and TAU constants
	private static final double PI = Math.PI;
	private static final double TAU = PI*2d;

	public MoveableCamera() {
	}

	// ###################################################################################
	// ################################ Position #########################################
	// ###################################################################################

	public void changeRadius(double delta) {
		this.radius += delta;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	// ###################################################################################
	// ################################ Rotation #########################################
	// ###################################################################################

	public void setRotation(double yaw, double pitch, double tilt) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.tilt = tilt;
		checkAngle();
	}

	public void rotateYaw(double delta) {
		yaw += delta;
		checkAngle();
	}
	public void rotatePitch(double delta) {
		pitch += delta;
		checkAngle();
	}
	public void rotateTilt(double delta) {
		tilt += delta;
		checkAngle();
	}

	// ###################################################################################
	// ################################ Update ###########################################
	// ###################################################################################

	protected void checkAngle() {
		if (yaw > TAU) {
			yaw -= TAU;
		}
		if (pitch > TAU) {
			pitch -= TAU;
		}
		if (tilt > PI) {
			tilt -= TAU;
		}
		if (yaw < 0) {
			yaw += TAU;
		}
		if (pitch < 0) {
			pitch += TAU;
		}
		if (tilt < -PI) {
			tilt += TAU;
		}
	}

	// ###################################################################################
	// ################################ View Matrix ######################################
	// ###################################################################################

	public Matrix4 getViewMatrix() {
		return  Transformations.rotateX(-tilt).times(
				Transformations.translate(new Vector3(0,0, -radius)).times(
				Transformations.rotateX(-pitch).times(
				Transformations.rotateY(-yaw))));
	}

	public Matrix4 getInvertedViewMatrix() {
		/*return  Transformations.rotateY(yaw).times(
				Transformations.rotateX(pitch).times(
				Transformations.translate(new Vector3(0,0, radius)).times(
				Transformations.rotateX(tilt))));*/
		return MatrixCalculations.invert(getViewMatrix());
	}

	/**
	 * Almost the same as the viewMatrix, but with different handling of camera tilt.
	 * This matrix is used to check FaceParts for their detail level depending on camera position and tilt.
	 * @return planetary LOD Matrix
	 */
	public Matrix4 getPlanetaryLODMatrix() {
		return  Transformations.rotateX(tilt * GraphicalConstants.PLANETARY_LOD_MATRIX_TILT_FACTOR).times(
				Transformations.translate(new Vector3(0,0, -radius)).times(
				Transformations.rotateX(-pitch).times(
				Transformations.rotateY(-yaw))));
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	/**
	 * @return Rotation around Y axis in radian.
	 */
	public double getYaw() {
		return yaw;
	}
	public void setYaw(double yaw) {
		this.yaw = yaw;
	}

	/**
	 * @return Rotation around X axis in radian.
	 */
	public double getPitch() {
		return pitch;
	}
	public void setPitch(double pitch) {
		this.pitch = pitch;
	}

	/**
	 * @return Rotaion in place around Y axis in radian.
	 */
	public double getTilt() {
		return tilt;
	}
	public void setTilt(double tilt) {
		this.tilt = tilt;
	}
}
