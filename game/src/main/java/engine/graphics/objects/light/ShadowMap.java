package engine.graphics.objects.light;

import constants.GraphicalConstants;
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
	private double cameraAngle = 0d;
	private double distance = 1d;
	private double scale = 1d;

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
	// ################################ Calculation ######################################
	// ###################################################################################

	private void actualizeMatrices() {
		viewMatrix =    Transformations.rotateY(-cameraAngle).times(
						Transformations.translate(new Vector3(0,0,-distance)).times(
						Transformations.rotateY(-lightAngle))
		);

		double size = scale * GraphicalConstants.SHADOWMAP_SCALE_FACTOR;
		orthographicProjection =    Projection.createOrthographicProjectionMatrix(
									-size, size, -size, size, zNear, zFar
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

	public double getCameraAngle() {
		return cameraAngle;
	}
	public void setCameraAngle(double cameraAngle) {
		this.cameraAngle = cameraAngle;
		modified = true;
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
