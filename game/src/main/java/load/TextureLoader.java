package load;

import de.matthiasmann.twl.utils.PNGDecoder;
import renderer.data.Texture;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class TextureLoader {

	public static Texture loadTexture(String fileName) {

		ByteBuffer buffer = null;
		System.out.println("loading " + fileName);

		try {
			PNGDecoder decoder = new PNGDecoder(new FileInputStream(fileName));

			buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
			decoder.decode(buffer, 4 * decoder.getWidth(), PNGDecoder.Format.RGBA);
			buffer.flip();

			return new Texture(decoder.getWidth(), decoder.getHeight(), buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
