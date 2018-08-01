package engine.graphics.objects.textures;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Texture {

	private int width;
	private int height;
	private ByteBuffer buffer;

	private int textureId;
	private boolean initialized = false;
	private int pixelFormat = GL11.GL_RGBA;

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

	public void initialize() {
		if (!initialized) {
			textureId = GL11.glGenTextures();

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

			//GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA /*GL11.GL_DEPTH_COMPONENT*/, width, height, 0, pixelFormat, GL11.GL_FLOAT, (ByteBuffer) null);
			//GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, pixelFormat, GL11.GL_UNSIGNED_BYTE, buffer);

			//GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

			//GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

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
