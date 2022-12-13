package engine.graphics.objects.movement;

import constants.GraphicalConstants;
import engine.graphics.objects.models.Material;
import engine.math.MatrixCalculations;
import engine.math.Transformations;
import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;
import engine.math.numericalObjects.Vector4;

public class MoveableObject implements SunDependantObject {

	protected Vector3 preRotation = null;
	protected Vector3 position = new Vector3(0,0,0);
	protected Vector3 scale = new Vector3(1,1,1);
	protected Vector3 rotation = new Vector3(0,0,0);

	private Matrix4 matrix;
	private boolean changed = true;

	private Material material = new Material();

	// TAU constant
	private static final double TAU = Math.PI*2d;

	public MoveableObject() {
	}

	// ###################################################################################
	// ################################ Material #########################################
	// ###################################################################################

	public void setMaterial(Material material) {
		this.material = material;
	}
	public Material getMaterial() {
		return material;
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
		changed = true;
	}

	/**
	 * Movement modifies the object's coordinates in respect to the internal coordinate axes.
	 * @param x translation on internal x axis
	 * @param y translation on internal x axis
	 * @param z translation on internal x axis
	 */
	public void move(double x, double y, double z) {
		Vector3[] internalAxes = Transformations.getInternalAxes(rotation);
		position.plusInplace(internalAxes[0].times(x))
				.plusInplace(internalAxes[1].times(y))
				.plusInplace(internalAxes[2].times(z));
		changed = true;
	}

	public void setPosition(double x, double y, double z) {
		position = new Vector3(x,y,z);
		changed = true;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
		changed = true;
	}

	// ###################################################################################
	// ################################ Scale ############################################
	// ###################################################################################

	public void scale(double s) {
		scale(s, s, s);
	}
	public void scale(double x, double y, double z) {
		Vector3 v = new Vector3(x,y,z);
		scale.timesElementwiseInplace(v);
		changed = true;
	}

	public void setScale(double x, double y, double z) {
		scale = new Vector3(x,y,z);
		changed = true;
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
		changed = true;
	}

	/**
	 * Rotates Object by given degree around origin of world coordinates around the x axis.
	 */
	public void rotateXAroundOrigin(double a) {
		Matrix4 rotationMatrix = Transformations.rotateX(a);
		rotation.plusInplace(new Vector3(a,0,0));
		position = rotationMatrix.times(new Vector4(position)).extractVector3();
		checkAngle();
		changed = true;
	}
	/**
	 * Rotates Object by given degree around origin of world coordinates around the y axis.
	 */
	public void rotateYAroundOrigin(double a) {
		Matrix4 rotationMatrix = Transformations.rotateY(a);
		rotation.plusInplace(new Vector3(0,a,0));
		position = rotationMatrix.times(new Vector4(position)).extractVector3();
		checkAngle();
		changed = true;
	}
	/**
	 * Rotates Object by given degree around origin of world coordinates around the z axis.
	 */
	public void rotateZAroundOrigin(double a) {
		Matrix4 rotationMatrix = Transformations.rotateZ(a);
		rotation.plusInplace(new Vector3(0,0,a));
		position = rotationMatrix.times(new Vector4(position)).extractVector3();
		checkAngle();
		changed = true;
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
		changed = true;
	}

	/**
	 * Rotates Object by given degree around center of own mesh around x axis.
	 */
	public void rotateX(double a) {
		rotation.plusInplace(new Vector3(a,0,0));
		checkAngle();
		changed = true;
	}
	/**
	 * Rotates Object by given degree around center of own mesh around y axis.
	 */
	public void rotateY(double a) {
		rotation.plusInplace(new Vector3(0,a,0));
		checkAngle();
		changed = true;
	}
	/**
	 * Rotates Object by given degree around center of own mesh around z axis.
	 */
	public void rotateZ(double a) {
		rotation.plusInplace(new Vector3(0,0,a));
		checkAngle();
		changed = true;
	}

	// ###################################################################################
	// ################################ PreRotation Around Own Axes ######################
	// ###################################################################################

	/**
	 * PreRotation can be used to simulate the rotation of an object around one of its internal axes.
	 * Works if you use only one of the dimensions, breaks when using more.
	 * @param x rotation around internal x axis
	 * @param y rotation around internal y axis
	 * @param z rotation around internal z axis
	 */
	public void setPreRotation(double x, double y, double z) {
		preRotation = new Vector3(x,y,z);
		changed = true;
	}

	/**
	 * Use this method to remove the PreRotation around the objects internal axes.
	 * Deleting the PreRotation gives a small performance boost.
	 */
	public void deletePreRotation() {
		preRotation = null;
		changed = true;
	}

	// ###################################################################################
	// ################################ Rotation #########################################
	// ###################################################################################

	/**
	 * Sets rotation of object to given exact value.
	 */
	public void setRotation(double x, double y, double z) {
		rotation = new Vector3(x,y,z);
		changed = true;
	}

	// ###################################################################################
	// ################################ Sun Companion ####################################
	// ###################################################################################

	@Override
	public void sunAngleIncrement(double angleStep) {
		rotateYAroundOrigin(angleStep);
	}
	@Override
	public void sunAngleReset() {
		setPosition(0,0, GraphicalConstants.SUN_DISTANCE);
		setRotation(0, 0, 0);
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
		if (changed) {
			if (preRotation == null) {
				matrix =    Transformations.translate(position).times(
							Transformations.rotate(rotation).times(
							Transformations.scale(scale)));
			} else {
				matrix =    Transformations.translate(position).times(
							Transformations.rotate(rotation).times(
							Transformations.scale(scale).times(
							Transformations.rotate(preRotation))));
			}
			changed = false;
		}
		return matrix;
	}

	public Matrix4 getInvertedWorldMatrix() {
		return MatrixCalculations.invert(matrix);
	}
}
