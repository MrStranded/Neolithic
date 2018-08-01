package engine.graphics.objects.light;

import constants.GraphicalConstants;
import engine.graphics.objects.textures.Texture;
import engine.math.Transformations;
import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class ShadowMap {

	private final int depthMapFBO;

	private final Texture depthMap;

	private Vector3 direction;
	private double distance = 1d;

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

	public Matrix4 getViewMatrix() {
		if (direction == null) {
			return new Matrix4();
		}

		Vector3 position = direction.times(-distance);

		double lightAngleX = Math.acos(direction.getZ());
		double lightAngleY = Math.asin(direction.getX());
		double lightAngleZ = 0;

		return  Transformations.translate(position.times(-1d)).times(
				Transformations.rotate(new Vector3(-lightAngleX, -lightAngleY, -lightAngleZ))
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

	public int getDepthMapFBO() {
		return depthMapFBO;
	}

	public Texture getDepthMap() {
		return depthMap;
	}

	public Vector3 getDirection() {
		return direction;
	}
	public void setDirection(Vector3 direction) {
		this.direction = direction;
	}

	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
}
