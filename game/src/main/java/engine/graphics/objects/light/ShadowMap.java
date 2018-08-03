package engine.graphics.objects.light;

import constants.GraphicalConstants;
import engine.graphics.objects.Camera;
import engine.graphics.objects.textures.Texture;
import engine.graphics.renderer.projection.Projection;
import engine.math.Transformations;
import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class ShadowMap {

	private final int depthMapFBO;
	private final Texture depthMap;

	private Matrix4 orthographicProjection;
	private Matrix4 viewMatrix;

	private double lightAngle = 0d;
	private double distance = 1d;
	private Camera camera;

	private float shadowStrength = 1f;

	private double zNear = -0.5d, zFar = 0.5d;

	boolean modified = true;

	public ShadowMap() throws Exception {
		// Create the depth map texture
		depthMap = new Texture(GraphicalConstants.SHADOWMAP_SIZE, GraphicalConstants.SHADOWMAP_SIZE, /*GL11.GL_RGBA*/ GL11.GL_DEPTH_COMPONENT);
		depthMap.initialize();

		// Create a FBO to render the depth map
		depthMapFBO = GL30.glGenFramebuffers();

		// Attach the depth map texture to the FBO
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, depthMapFBO);

		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, /*GL30.GL_COLOR_ATTACHMENT0*/ GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthMap.getTextureId(), 0);

		// Set only depth
		GL11.glDrawBuffer(/*GL30.GL_COLOR_ATTACHMENT0*/ GL11.GL_NONE);
		GL11.glReadBuffer(/*GL30.GL_COLOR_ATTACHMENT0*/ GL11.GL_NONE);

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

			// ---------------------------------------- shadow strength
			double poleShadow = Math.abs(Math.sin(camera.getPitch())); // 1 at poles, 0 at equator

			double brightSpot = GraphicalConstants.SHADOWMAP_BRIGHT_SPOT_SIZE;
			double angle = lightAngle + camera.getYaw();
			if (angle < 0) {
				angle += Math.PI*2d;
			}
			if (angle >= Math.PI*2d) {
				angle -= Math.PI*2d;
			}
			double equatorShadow = (Math.cos(angle) + 1d) / 2d;
			if (angle < Math.PI/brightSpot || angle > Math.PI*2d - Math.PI/brightSpot) {
				equatorShadow = equatorShadow - Math.cos(angle*brightSpot);
			}

			shadowStrength = (float) ((1d - poleShadow) * equatorShadow + poleShadow);

			// ---------------------------------------- scaling factor
			double max = GraphicalConstants.SHADOWMAP_MAX_SCALING;
			double min = GraphicalConstants.SHADOWMAP_MIN_SCALING;

			double radiusFactor = 0d;
			if (camera.getRadius() > distance) {
				radiusFactor = Math.sqrt(Math.sqrt(camera.getRadius() - distance));
			}

			double scaleFactor = max - radiusFactor * (max - min);
			if (scaleFactor > max) {
				scaleFactor = max;
			}
			if (scaleFactor < min) {
				scaleFactor = min;
			}
			Vector3 shadowScale = new Vector3(scaleFactor, scaleFactor, 1d);

			// ---------------------------------------- position
			double yaw = camera.getYaw();
			double pitch = -camera.getPitch();
			double heightFactor = Math.cos(pitch);
			Vector3 position = new Vector3(Math.sin(yaw) * heightFactor, Math.sin(pitch), Math.cos(yaw) * heightFactor).times(-distance);

			viewMatrix =    Transformations.scale(shadowScale).times(
							Transformations.rotateY(-lightAngle).times(
							Transformations.translate(position)
			));
		} else {
			shadowStrength = 0f;

			viewMatrix = new Matrix4();
		}

		double size = distance * GraphicalConstants.SHADOWMAP_SCALE_FACTOR;
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
