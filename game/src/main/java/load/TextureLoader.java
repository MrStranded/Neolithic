package load;

import de.matthiasmann.twl.utils.PNGDecoder;
import engine.graphics.objects.textures.Texture;
import engine.parser.utils.Logger;
import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class TextureLoader {

	private static Map<String, Texture> cache = new HashMap<>();

	public static Texture loadTexture(String partialPath) {
		String filePath = AssetResolver.getTexturePath(partialPath);

		return getOrLoadTexture(filePath);
	}

	private static Texture getOrLoadTexture(String path) {
		Texture texture = cache.get(path);

		if (texture == null) {
			try {
				ByteBuffer buffer = null;
				Logger.debug("loading texture " + path);

				PNGDecoder decoder = new PNGDecoder(new FileInputStream(path));

				buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
				decoder.decode(buffer, 4 * decoder.getWidth(), PNGDecoder.Format.RGBA);
				buffer.flip();

				texture = new Texture(decoder.getWidth(), decoder.getHeight(), buffer);
				cache.put(path, texture);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return texture;
	}

	public static void clear() {
		cache.clear();
	}
}
