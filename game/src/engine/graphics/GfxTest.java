package engine.graphics;

/**
 * Created by Michael on 10.11.2017.
 */
public class GfxTest extends ExampleBase {
	/** Keep a reference to the box to be able to rotate it each frame. */
	private Mesh box;

	/** Rotation matrix for the spinning box. */
	private final Matrix3 rotate = new Matrix3();

	/** Angle of rotation for the box. */
	private double angle = 0;

	/** Axis to rotate the box around. */
	private final Vector3 axis = new Vector3(1, 1, 0.5f).normalizeLocal();

	public static void main(final String[] args) {
		start(BoxExample.class);
	}

	@Override
	protected void updateExample(final ReadOnlyTimer timer) {
		// Update the angle using the current tpf to rotate at a constant speed.
		angle += timer.getTimePerFrame() * 50;
		// Wrap the angle to keep it inside 0-360 range
		angle %= 360;

		// Update the rotation matrix using the angle and rotation axis.
		rotate.fromAngleNormalAxis(angle * MathUtils.DEG_TO_RAD, axis);
		// Update the box rotation using the rotation matrix.
		box.setRotation(rotate);
	}

	@Override
	protected void initExample() {
		_canvas.setTitle("Box Example");

		// Create a new box centered at (0,0,0) with width/height/depth of size 10.
		box = new Box("Box", new Vector3(0, 0, 0), 5, 5, 5);
		// Set a bounding box for frustum culling.
		box.setModelBound(new BoundingBox());
		// Move the box out from the camera 15 units.
		box.setTranslation(new Vector3(0, 0, -15));
		// Give the box some nice colors.
		box.setRandomColors();
		// Attach the box to the scenegraph root.
		_root.attachChild(box);

		// Add a texture to the box.
		final TextureState ts = new TextureState();
		ts.setTexture(TextureManager.load("images/ardor3d_white_256.jpg", Texture.MinificationFilter.Trilinear, true));
		box.setRenderState(ts);

		// Add a material to the box, to show both vertex color and lighting/shading.
		final MaterialState ms = new MaterialState();
		ms.setColorMaterial(ColorMaterial.Diffuse);
		box.setRenderState(ms);
	}
}
