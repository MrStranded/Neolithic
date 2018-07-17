package engine.graphics.objects.models;

import java.nio.ByteBuffer;

public class Texture {

	private int width;
	private int height;
	private ByteBuffer buffer;

	public Texture(int width, int height, ByteBuffer buffer) {

		this.width = width;
		this.height = height;
		this.buffer = buffer;
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

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
