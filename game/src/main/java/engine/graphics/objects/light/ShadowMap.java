package engine.graphics.objects.light;

import constants.GraphicalConstants;
import constants.TopologyConstants;
import engine.graphics.objects.Camera;
import engine.graphics.objects.textures.Texture;
import engine.graphics.renderer.projection.Projection;
import engine.math.Transformations;
import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;
import engine.parser.utils.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class ShadowMap {

	private final double TAU = Math.PI*2d;

	private final int depthMapFBO;
	private final Texture depthMap;

	private Matrix4 orthographicProjection;
	private Matrix4 viewMatrix;

	private double lightAngle = 0d;
	private double distance = 1d;
	private Camera camera;

	private float shadowStrength = 1f;
	private float epsilon = 0.005f;

	private double zNear = -0.25d, zFar = 0.125d;

	boolean modified = true;

	public ShadowMap() throws Exception {
		// Create the depth map texture
		depthMap = new Texture(GraphicalConstants.SHADOWMAP_SIZE, GraphicalConstants.SHADOWMAP_SIZE, GL11.GL_DEPTH_COMPONENT);
		depthMap.initialize();

		// Create a FBO to render the depth map
		depthMapFBO = GL30.glGenFramebuffers();

		// Attach the depth map texture to the FBO
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, depthMapFBO);

		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthMap.getTextureId(), 0);

		// Set only depth
		GL11.glDrawBuffer(GL11.GL_NONE);
		GL11.glReadBuffer(GL11.GL_NONE);

		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			throw new Exception("Could not create FrameBuffer. Error code: " + GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER));
		}

		// Unbind
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	// ###################################################################################
	// ################################ Camera Movement ##################################
	// ###################################################################################

	public void cameraChangedPosition() {
		modified = true;
	}

	// ###################################################################################
	// ################################ Calculation ######################################
	// ###################################################################################

	/**
	 * The calculation of the values is extremely specific to the given planet situation.
	 * These Shadow Maps really only work in the one scenario of a planet being lit.
	 */
	private void actualize() {
		if (camera != null) {
			// ---------------------------------------- radius factor
			double radiusFactor = 0d;
			final double radiusScope = GraphicalConstants.SHADOWMAP_RADIUS_SCOPE;
			double radius = Math.min(camera.getRadius(), distance + radiusScope); // to ensure that radiusFactor is between 0 and 1
			if (radius > distance) {
				radiusFactor = Math.sqrt(Math.sqrt((radius - distance) / radiusScope));
			}

			// ---------------------------------------- znear / zfar
			final double zValue = Math.min(
					GraphicalConstants.SHADOWMAP_MIN_ZVALUE +
					(GraphicalConstants.SHADOWMAP_MAX_ZVALUE - GraphicalConstants.SHADOWMAP_MIN_ZVALUE) * radiusFactor,
					0.5d);
			zNear = Math.min( -zValue, -distance * 1.001); // prevent clipping of inner shadow plane
			zFar = zValue;

			// ---------------------------------------- epsilon (depth bias)
			final double maxEpsilon = GraphicalConstants.SHADOWMAP_MAX_EPSILON;
			final double minEpsilon = GraphicalConstants.SHADOWMAP_MIN_EPSILON;

			double epsilonD = minEpsilon + radiusFactor*radiusFactor * (maxEpsilon - minEpsilon);
			epsilonD = Math.min(maxEpsilon, Math.max(minEpsilon, epsilonD));
			epsilon = (float) epsilonD;

			// ---------------------------------------- shadow strength
			shadowStrength = 1f;

			// ---------------------------------------- scaling factor
			final double max = GraphicalConstants.SHADOWMAP_MAX_SCALING;
			final double min = GraphicalConstants.SHADOWMAP_MIN_SCALING;

			double scaleFactor = max - radiusFactor * (max - min);
			scaleFactor = Math.min(max, Math.max(min, scaleFactor));
			Vector3 shadowScale = new Vector3(scaleFactor, scaleFactor, 1d);

			// ---------------------------------------- position
			final double yaw = camera.getYaw();
			final double pitch = -camera.getPitch() + camera.getTilt() * GraphicalConstants.PLANETARY_LOD_MATRIX_TILT_FACTOR * 0.5d;
			final double heightFactor = Math.cos(pitch);
			Vector3 position = new Vector3(
					Math.sin(yaw) * heightFactor,
					Math.sin(pitch),
					Math.cos(yaw) * heightFactor
			).times(-distance);

			viewMatrix =    Transformations.scale(shadowScale).times(
							Transformations.rotateY(-lightAngle).times(
							Transformations.translate(position)
			));
		} else {
			shadowStrength = 0f;

			viewMatrix = new Matrix4();
		}

		final double size = distance * (TopologyConstants.PLANET_MINIMUM_HEIGHT + TopologyConstants.PLANET_MAXIMUM_HEIGHT) / TopologyConstants.PLANET_MINIMUM_HEIGHT;
		orthographicProjection =    Projection.createOrthographicProjectionMatrix(
									-size, size, size, -size, zNear*distance, zFar*distance
		);
	}

	// ###################################################################################
	// ################################ Clean Up #########################################
	// ###################################################################################

	public void cleanup() {
		GL30.glDeleteFramebuffers(depthMapFBO);
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public Matrix4 getViewMatrix() {
		if (modified) {
			actualize();
		}
		return viewMatrix;
	}

	public Matrix4 getOrthographicProjection() {
		if (modified) {
			actualize();
		}
		return orthographicProjection;
	}

	public float getEpsilon() {
		return epsilon;
	}

	public float getShadowStrength() {
		return shadowStrength;
	}

	public int getDepthMapFBO() {
		return depthMapFBO;
	}

	public Texture getDepthMap() {
		return depthMap;
	}

	public double getLightAngle() {
		return lightAngle;
	}
	public void setLightAngle(double lightAngle) {
		this.lightAngle = lightAngle;
		modified = true;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
		modified = true;
	}

	public double getzNear() {
		return zNear;
	}
	public void setzNear(double zNear) {
		this.zNear = zNear;
		modified = true;
	}

	public double getzFar() {
		return zFar;
	}
	public void setzFar(double zFar) {
		this.zFar = zFar;
		modified = true;
	}
}
