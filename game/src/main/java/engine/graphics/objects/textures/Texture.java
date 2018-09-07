package engine.graphics.objects.textures;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Texture {

	private int width;
	private int height;

	private int pixelFormat = GL11.GL_RGBA;
	private ByteBuffer buffer = null;
	private int textureId;

	private boolean shadowMap;

	private boolean initialized = false;

	public Texture(int width, int height, ByteBuffer buffer) {
		this.width = width;
		this.height = height;
		this.buffer = buffer;
	}

	public Texture(int width, int height, InputStream inputStream) {
		this.width = width;
		this.height = height;
		try {
			buffer = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * A constructor used specifically for shadow maps. Do not use it for anything else!
	 * @param width
	 * @param height
	 * @param pixelFormat
	 */
	public Texture(int width, int height, int pixelFormat) {
		this.width = width;
		this.height = height;
		this.pixelFormat = pixelFormat;

		shadowMap = true;
	}

	public void initialize() {
		if (!initialized) {
			int dataType = GL11.GL_UNSIGNED_BYTE;
			if (shadowMap) {
				dataType = GL11.GL_FLOAT;
			}

			textureId = GL11.glGenTextures();

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

			// another option for the last parameter would be: GL_LINEAR_MIPMAP_LINEAR (attention: much more expensive)
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

			if (shadowMap) {
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);

				float color[] = { 1.0f, 1.0f, 1.0f, 1.0f };
				GL11.glTexParameterfv(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_BORDER_COLOR, color);
			} else {
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			}

			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, pixelFormat, width, height, 0, pixelFormat, dataType, buffer);

			// texture mip map (not used with GL_NEAREST)
			//GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

			initialized = true;
		}
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public int getTextureId() {
		return textureId;
	}

	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

}
