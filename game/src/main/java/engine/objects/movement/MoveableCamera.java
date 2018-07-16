package engine.objects.movement;

import math.Matrix4;
import math.Vector3;
import math.Vector4;
import math.utils.Transformations;

public class MoveableCamera {

	protected Vector3 worldPosition = new Vector3(0,0,0);
	protected Vector3 worldRotation = new Vector3(0,0,0);

	// this matrix holds the complete worldPosition, scale and worldRotation information at once
	protected Matrix4 matrix = new Matrix4();

	// tau constant
	private static final double tau = Math.PI*2d;

	public MoveableCamera() {
	}

	// ###################################################################################
	// ################################ Position #########################################
	// ###################################################################################

	/**
	 * Translation modifies the object's coordinates in respect to the global coordinate axes.
	 * @param x translation on global x axis
	 * @param y translation on global x axis
	 * @param z translation on global x axis
	 */
	public void translate(double x, double y, double z) {

		Matrix4 cameraRotation = Transformations.rotate(worldRotation);
		Vector3 xInternal = cameraRotation.times(new Vector4(1d,0,0,0)).extractVector3();
		Vector3 yInternal = cameraRotation.times(new Vector4(0,1d,0,0)).extractVector3();
		Vector3 zInternal = cameraRotation.times(new Vector4(0,0,1d,0)).extractVector3();
		worldPosition.plusInplace(xInternal.times(-x)).plusInplace(yInternal.times(-y)).plusInplace(zInternal.times(-z));
		update();
	}

	/**
	 * Movement modifies the object's coordinates in respect to the internal coordinate axes.
	 * @param x translation on internal x axis
	 * @param y translation on internal x axis
	 * @param z translation on internal x axis
	 */
	public void move(double x, double y, double z) {

		Vector3 v = new Vector3(-x,-y,-z);
		worldPosition.plusInplace(v);
		update();
	}

	public void setPosition(double x, double y, double z) {

		worldPosition = new Vector3(-x,-y,-z);
		update();
	}

	// ###################################################################################
	// ################################ Rotation Around Own Center #######################
	// ###################################################################################

	/**
	 * Rotates Camera by given degrees around origin of world coordinates.
	 */
	public void rotate(double x, double y, double z) {

		Vector3 v = new Vector3(x,y,z);
		Matrix4 worldRotationMatrix = Transformations.rotate(v);
		worldRotation.plusInplace(v);
		worldPosition = worldRotationMatrix.times(new Vector4(worldPosition)).extractVector3();
		matrix = worldRotationMatrix.times(matrix);
	}

	/**
	 * Rotates Camera by given degree around origin of world coordinates around the x axis.
	 */
	public void rotateX(double a) {

		Matrix4 worldRotationMatrix = Transformations.rotateX(a);
		worldRotation.plusInplace(new Vector3(a,0,0));
		worldPosition = worldRotationMatrix.times(new Vector4(worldPosition)).extractVector3();
		matrix = worldRotationMatrix.times(matrix);
	}
	/**
	 * Rotates Camera by given degree around origin of world coordinates around the y axis.
	 */
	public void rotateY(double a) {

		Matrix4 worldRotationMatrix = Transformations.rotateY(a);
		worldRotation.plusInplace(new Vector3(0,a,0));
		worldPosition = worldRotationMatrix.times(new Vector4(worldPosition)).extractVector3();
		matrix = worldRotationMatrix.times(matrix);
	}
	/**
	 * Rotates Camera by given degree around origin of world coordinates around the z axis.
	 */
	public void rotateZ(double a) {

		Matrix4 worldRotationMatrix = Transformations.rotateZ(a);
		worldRotation.plusInplace(new Vector3(0,0,a));
		worldPosition = worldRotationMatrix.times(new Vector4(worldPosition)).extractVector3();
		matrix = worldRotationMatrix.times(matrix);
	}

	// ###################################################################################
	// ################################ Rotation Around Origin ###########################
	// ###################################################################################

	/**
	 * Rotates Camera by given degrees around center of own mesh.
	 */
	public void rotateAroundOrigin(double x, double y, double z) {

		Vector3 v = new Vector3(-x,-y,-z);
		worldRotation.plusInplace(v);
		update();
	}

	/**
	 * Rotates Camera by given degree around center of own mesh around x axis.
	 */
	public void rotateXAroundOrigin(double a) {

		worldRotation.plusInplace(new Vector3(-a,0,0));
		update();
	}
	/**
	 * Rotates Camera by given degree around center of own mesh around y axis.
	 */
	public void rotateYAroundOrigin(double a) {

		worldRotation.plusInplace(new Vector3(0,-a,0));
		update();
	}
	/**
	 * Rotates Camera by given degree around center of own mesh around z axis.
	 */
	public void rotateZAroundOrigin(double a) {

		worldRotation.plusInplace(new Vector3(0,0,-a));
		update();
	}

	// ###################################################################################
	// ################################ Rotation #########################################
	// ###################################################################################

	/**
	 * Sets worldRotation of object to given exact value.
	 */
	public void setRotation(double x, double y, double z) {

		worldRotation = new Vector3(x,y,z);
		update();
	}

	// ###################################################################################
	// ################################ Update ###########################################
	// ###################################################################################

	protected void update() {

		checkAngle();

		matrix =    Transformations.translate(worldPosition).times(
					Transformations.rotate(worldRotation));
	}

	protected void checkAngle() {

		if (worldRotation.getX() > tau) {
			worldRotation.setX(worldRotation.getX()-tau);
		}
		if (worldRotation.getY() > tau) {
			worldRotation.setY(worldRotation.getY()-tau);
		}
		if (worldRotation.getZ() > tau) {
			worldRotation.setZ(worldRotation.getZ()-tau);
		}
		if (worldRotation.getX()<0) {
			worldRotation.setX(worldRotation.getX()+tau);
		}
		if (worldRotation.getY()<0) {
			worldRotation.setY(worldRotation.getY()+tau);
		}
		if (worldRotation.getZ()<0) {
			worldRotation.setZ(worldRotation.getZ()+tau);
		}
	}
}
