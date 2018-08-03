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
	private double scale = 1d;
	private Camera camera;

	private double zNear = 1d, zFar = 10d;

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

	private void actualizeMatrices() {
		if (camera != null) {
			double max = 12d;
			double min = 1d;

			double zmax = 0.5d;
			double zmin = 0.01d;

			double radiusFactor = 0d;
			if (camera.getRadius() > 1d) {
				radiusFactor = Math.sqrt(Math.sqrt(camera.getRadius() - 1d));
			}

			double scaleFactor = max - radiusFactor * (max - min);
			if (scaleFactor > max) {
				scaleFactor = max;
			}
			if (scaleFactor < min) {
				scaleFactor = min;
			}
			Vector3 shadowScale = new Vector3(scale * scaleFactor, scale * scaleFactor, scale);

			double zFactor = zmin + radiusFactor * (zmax - zmin);
			if (zFactor < zmin) {
				zFactor = zmin;
			}
			if (zFactor > zmax) {
				zFactor = zmax;
			}

			//zNear = -zFactor;
			//zFar = zFactor;

			double yaw = camera.getYaw();
			double pitch = -camera.getPitch();// + camera.getTilt()/4d;
			double heightFactor = Math.cos(pitch);
			Vector3 position = new Vector3(Math.sin(yaw) * heightFactor, Math.sin(pitch), Math.cos(yaw) * heightFactor).times(-distance);

			viewMatrix =
					Transformations.scale(shadowScale).times(
					Transformations.rotateY(-lightAngle).times(
							Transformations.translate(position)
			));
		} else {
			viewMatrix = new Matrix4();
		}

		double size = GraphicalConstants.SHADOWMAP_SCALE_FACTOR;
		orthographicProjection =    Projection.createOrthographicProjectionMatrix(
									-size, size, size, -size, zNear, zFar
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
			actualizeMatrices();
		}
		return viewMatrix;
	}

	public Matrix4 getOrthographicProjection() {
		if (modified) {
			actualizeMatrices();
		}
		return orthographicProjection;
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

	public double getScale() {
		return scale;
	}
	public void setScale(double scale) {
		this.scale = scale;
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
