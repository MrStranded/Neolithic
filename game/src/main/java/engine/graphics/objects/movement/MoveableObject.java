package engine.graphics.objects.movement;

import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;
import engine.math.numericalObjects.Vector4;
import engine.math.Transformations;

public class MoveableObject {

	protected Vector3 position = new Vector3(0,0,0);
	protected Vector3 scale = new Vector3(1,1,1);
	protected Vector3 rotation = new Vector3(0,0,0);

	private Matrix4 matrix;

	// TAU constant
	private static final double TAU = Math.PI*2d;

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
	}

	public void setPosition(double x, double y, double z) {
		position = new Vector3(x,y,z);
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	// ###################################################################################
	// ################################ Scale ############################################
	// ###################################################################################

	public void scale(double x, double y, double z) {
		Vector3 v = new Vector3(x,y,z);
		scale.timesElementwiseInplace(v);
	}

	public void setScale(double x, double y, double z) {
		scale = new Vector3(x,y,z);
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
		checkAngle();
	}

	/**
	 * Rotates Object by given degree around origin of world coordinates around the x axis.
	 */
	public void rotateXAroundOrigin(double a) {
		Matrix4 rotationMatrix = Transformations.rotateX(a);
		rotation.plusInplace(new Vector3(a,0,0));
		position = rotationMatrix.times(new Vector4(position)).extractVector3();
		checkAngle();
	}
	/**
	 * Rotates Object by given degree around origin of world coordinates around the y axis.
	 */
	public void rotateYAroundOrigin(double a) {
		Matrix4 rotationMatrix = Transformations.rotateY(a);
		rotation.plusInplace(new Vector3(0,a,0));
		position = rotationMatrix.times(new Vector4(position)).extractVector3();
		checkAngle();
	}
	/**
	 * Rotates Object by given degree around origin of world coordinates around the z axis.
	 */
	public void rotateZAroundOrigin(double a) {
		Matrix4 rotationMatrix = Transformations.rotateZ(a);
		rotation.plusInplace(new Vector3(0,0,a));
		position = rotationMatrix.times(new Vector4(position)).extractVector3();
		checkAngle();
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
		checkAngle();
	}

	/**
	 * Rotates Object by given degree around center of own mesh around x axis.
	 */
	public void rotateX(double a) {
		rotation.plusInplace(new Vector3(a,0,0));
		checkAngle();
	}
	/**
	 * Rotates Object by given degree around center of own mesh around y axis.
	 */
	public void rotateY(double a) {
		rotation.plusInplace(new Vector3(0,a,0));
		checkAngle();
	}
	/**
	 * Rotates Object by given degree around center of own mesh around z axis.
	 */
	public void rotateZ(double a) {
		rotation.plusInplace(new Vector3(0,0,a));
		checkAngle();
	}

	// ###################################################################################
	// ################################ Rotation #########################################
	// ###################################################################################

	/**
	 * Sets rotation of object to given exact value.
	 */
	public void setRotation(double x, double y, double z) {
		rotation = new Vector3(x,y,z);
	}

	// ###################################################################################
	// ################################ Update ###########################################
	// ###################################################################################

	protected void checkAngle() {
		if (rotation.getX() > TAU) {
			rotation.setX(rotation.getX()-TAU);
		}
		if (rotation.getY() > TAU) {
			rotation.setY(rotation.getY()-TAU);
		}
		if (rotation.getZ() > TAU) {
			rotation.setZ(rotation.getZ()-TAU);
		}
		if (rotation.getX()<0) {
			rotation.setX(rotation.getX()+TAU);
		}
		if (rotation.getY()<0) {
			rotation.setY(rotation.getY()+TAU);
		}
		if (rotation.getZ()<0) {
			rotation.setZ(rotation.getZ()+TAU);
		}
	}

	// ###################################################################################
	// ################################ Position Matrix ##################################
	// ###################################################################################

	public Matrix4 getWorldMatrix() {
		return  Transformations.translate(position).times(
				Transformations.rotate(rotation).times(
				Transformations.scale(scale)));
	}
}
