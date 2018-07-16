package engine.objects.movement;

import math.Matrix4;
import math.Vector3;
import math.Vector4;
import math.utils.Transformations;

public class MoveableObject {

	protected Vector3 position = new Vector3(0,0,0);
	protected Vector3 scale = new Vector3(1,1,1);
	protected Vector3 rotation = new Vector3(0,0,0);

	// this matrix holds the complete position, scale and rotation information at once
	protected Matrix4 matrix = new Matrix4();

	// tau constant
	private static final double tau = Math.PI*2d;

	public MoveableObject() {
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

		Vector3 v = new Vector3(x,y,z);
		position.plusInplace(v);
		update();
	}

	/**
	 * Movement modifies the object's coordinates in respect to the internal coordinate axes.
	 * @param x translation on internal x axis
	 * @param y translation on internal x axis
	 * @param z translation on internal x axis
	 */
	public void move(double x, double y, double z) {

		Vector3[] internalAxes = Transformations.getInternalAxes(rotation);

		position.plusInplace(internalAxes[0].times(x)).plusInplace(internalAxes[0].times(y)).plusInplace(internalAxes[0].times(z));
		update();
	}

	public void setPosition(double x, double y, double z) {

		position = new Vector3(x,y,z);
		update();
	}

	// ###################################################################################
	// ################################ Scale ############################################
	// ###################################################################################

	public void scale(double x, double y, double z) {

		Vector3 v = new Vector3(x,y,z);
		scale.timesElementwiseInplace(v);
		update();
	}

	public void setScale(double x, double y, double z) {

		scale = new Vector3(x,y,z);
		update();
	}

	// ###################################################################################
	// ################################ Rotation Around Origin ###########################
	// ###################################################################################

	/**
	 * Rotates Object by given degrees around origin of world coordinates.
	 */
	public void rotateAroundOrigin(double x, double y, double z) {

		Vector3 v = new Vector3(x,y,z);
		Matrix4 rotationMatrix = Transformations.rotate(v);
		rotation.plusInplace(v);
		position = rotationMatrix.times(new Vector4(position)).extractVector3();
		matrix = rotationMatrix.times(matrix);
	}

	/**
	 * Rotates Object by given degree around origin of world coordinates around the x axis.
	 */
	public void rotateXAroundOrigin(double a) {

		Matrix4 rotationMatrix = Transformations.rotateX(a);
		rotation.plusInplace(new Vector3(a,0,0));
		position = rotationMatrix.times(new Vector4(position)).extractVector3();
		matrix = rotationMatrix.times(matrix);
	}
	/**
	 * Rotates Object by given degree around origin of world coordinates around the y axis.
	 */
	public void rotateYAroundOrigin(double a) {

		Matrix4 rotationMatrix = Transformations.rotateY(a);
		rotation.plusInplace(new Vector3(0,a,0));
		position = rotationMatrix.times(new Vector4(position)).extractVector3();
		matrix = rotationMatrix.times(matrix);
	}
	/**
	 * Rotates Object by given degree around origin of world coordinates around the z axis.
	 */
	public void rotateZAroundOrigin(double a) {

		Matrix4 rotationMatrix = Transformations.rotateZ(a);
		rotation.plusInplace(new Vector3(0,0,a));
		position = rotationMatrix.times(new Vector4(position)).extractVector3();
		matrix = rotationMatrix.times(matrix);
	}

	// ###################################################################################
	// ################################ Rotation Around Internal Axes ####################
	// ###################################################################################

	/**
	 * Rotates object around internal x axis.
	 * Attention: Rotations around internal axes are NOT saved and disappear with the next update of the matrix!
	 * @param a angle in radian
	 */
	public void rotateXLocal(double a) {

		Vector3 internalAxis = Transformations.getInternalXAxis(rotation);
		matrix = Transformations.rotateAroundVector(internalAxis,a).times(matrix);
	}
	/**
	 * Rotates object around internal y axis.
	 * Attention: Rotations around internal axes are NOT saved and disappear with the next update of the matrix!
	 * @param a angle in radian
	 */
	public void rotateYLocal(double a) {

		Vector3 internalAxis = Transformations.getInternalYAxis(rotation);
		matrix = Transformations.rotateAroundVector(internalAxis,a).times(matrix);
	}
	/**
	 * Rotates object around internal z axis.
	 * Attention: Rotations around internal axes are NOT saved and disappear with the next update of the matrix!
	 * @param a angle in radian
	 */
	public void rotateZLocal(double a) {

		Vector3 internalAxis = Transformations.getInternalZAxis(rotation);
		matrix = Transformations.rotateAroundVector(internalAxis,a).times(matrix);
	}

	// ###################################################################################
	// ################################ Rotation Around Self Center ######################
	// ###################################################################################

	/**
	 * Rotates Object by given degrees around center of own mesh.
	 */
	public void rotate(double x, double y, double z) {

		Vector3 v = new Vector3(x,y,z);
		rotation.plusInplace(v);
		update();
	}

	/**
	 * Rotates Object by given degree around center of own mesh around x axis.
	 */
	public void rotateX(double a) {

		rotation.plusInplace(new Vector3(a,0,0));
		update();
	}
	/**
	 * Rotates Object by given degree around center of own mesh around y axis.
	 */
	public void rotateY(double a) {

		rotation.plusInplace(new Vector3(0,a,0));
		update();
	}
	/**
	 * Rotates Object by given degree around center of own mesh around z axis.
	 */
	public void rotateZ(double a) {

		rotation.plusInplace(new Vector3(0,0,a));
		update();
	}

	// ###################################################################################
	// ################################ Rotation #########################################
	// ###################################################################################

	/**
	 * Sets rotation of object to given exact value.
	 */
	public void setRotation(double x, double y, double z) {

		rotation = new Vector3(x,y,z);
		update();
	}

	// ###################################################################################
	// ################################ Update ###########################################
	// ###################################################################################

	protected void update() {

		checkAngle();

		matrix =    Transformations.translate(position).times(
					Transformations.rotate(rotation).times(
					Transformations.scale(scale)));
	}

	protected void checkAngle() {

		if (rotation.getX() > tau) {
			rotation.setX(rotation.getX()-tau);
		}
		if (rotation.getY() > tau) {
			rotation.setY(rotation.getY()-tau);
		}
		if (rotation.getZ() > tau) {
			rotation.setZ(rotation.getZ()-tau);
		}
		if (rotation.getX()<0) {
			rotation.setX(rotation.getX()+tau);
		}
		if (rotation.getY()<0) {
			rotation.setY(rotation.getY()+tau);
		}
		if (rotation.getZ()<0) {
			rotation.setZ(rotation.getZ()+tau);
		}
	}
}
