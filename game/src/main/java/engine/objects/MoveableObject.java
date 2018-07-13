package engine.objects;

import engine.renderer.data.Mesh;
import math.Matrix4;
import math.Vector3;
import math.Vector4;
import math.utils.MatrixTransformations;

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

		Matrix4 cameraRotation = MatrixTransformations.rotate(rotation);
		Vector3 xInternal = cameraRotation.times(new Vector4(1d,0,0,0)).extractVector3();
		Vector3 yInternal = cameraRotation.times(new Vector4(0,1d,0,0)).extractVector3();
		Vector3 zInternal = cameraRotation.times(new Vector4(0,0,1d,0)).extractVector3();
		position.plusInplace(xInternal.times(x)).plusInplace(yInternal.times(y)).plusInplace(zInternal.times(z));
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

	public void rotateAroundOrigin(double x, double y, double z) {

		Vector3 v = new Vector3(x,y,z);
		Matrix4 rotationMatrix = MatrixTransformations.rotate(v);
		rotation.plusInplace(v);
		position = rotationMatrix.times(new Vector4(position)).extractVector3();
		matrix = rotationMatrix.times(matrix);
		optionalUpdate();
	}

	public void rotateXAroundOrigin(double a) {

		Matrix4 rotationMatrix = MatrixTransformations.rotateX(a);
		rotation.plusInplace(new Vector3(a,0,0));
		position = rotationMatrix.times(new Vector4(position)).extractVector3();
		matrix = rotationMatrix.times(matrix);
		optionalUpdate();
	}
	public void rotateYAroundOrigin(double a) {

		Matrix4 rotationMatrix = MatrixTransformations.rotateY(a);
		rotation.plusInplace(new Vector3(0,a,0));
		position = rotationMatrix.times(new Vector4(position)).extractVector3();
		matrix = rotationMatrix.times(matrix);
		optionalUpdate();
	}
	public void rotateZAroundOrigin(double a) {

		Matrix4 rotationMatrix = MatrixTransformations.rotateZ(a);
		rotation.plusInplace(new Vector3(0,0,a));
		position = rotationMatrix.times(new Vector4(position)).extractVector3();
		matrix = rotationMatrix.times(matrix);
		optionalUpdate();
	}

	// ###################################################################################
	// ################################ Rotation Around Self Center ######################
	// ###################################################################################

	public void rotate(double x, double y, double z) {

		Vector3 v = new Vector3(x,y,z);
		rotation.plusInplace(v);
		update();
	}

	public void rotateX(double a) {

		rotation.plusInplace(new Vector3(a,0,0));
		update();
	}
	public void rotateY(double a) {

		rotation.plusInplace(new Vector3(0,a,0));
		update();
	}
	public void rotateZ(double a) {

		rotation.plusInplace(new Vector3(0,0,a));
		update();
	}

	// ###################################################################################
	// ################################ Rotation #########################################
	// ###################################################################################

	public void setRotation(double x, double y, double z) {

		rotation = new Vector3(x,y,z);
		update();
	}

	// ###################################################################################
	// ################################ Update ###########################################
	// ###################################################################################

	/**
	 * Will be implemented by Camera, but not by GraphicalObject.
	 */
	protected void optionalUpdate() {
	}

	protected void update() {

		checkAngle();

		matrix =    MatrixTransformations.translate(position).times(
					MatrixTransformations.rotate(rotation).times(
					MatrixTransformations.scale(scale)));
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