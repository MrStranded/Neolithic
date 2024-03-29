package engine.graphics.objects.textures;

import constants.GraphicalConstants;
import constants.ResourcePathConstants;
import engine.parser.utils.Logger;
import load.TextureLoader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;

public class FontTexture {

	private Texture texture;
	private int width, height;

	private Font font;
	private String charSetName;

	private HashMap<Character, CharInfo> charMap;

	public FontTexture(Font font, String charSetName) throws IOException {
		this.font = font;
		this.charSetName = charSetName;

		charMap = new HashMap<>();

		texture = buildTexture();
	}

	// ###################################################################################
	// ################################ Generation #######################################
	// ###################################################################################

	private Texture buildTexture() throws IOException {
		// Get the font metrics for each character for the selected font by using an image
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setFont(font);
		FontMetrics fontMetrics = graphics.getFontMetrics();

		String allChars = getAllAvailableChars(charSetName);
		this.width = 0;
		this.height = fontMetrics.getHeight();
		for (char c : allChars.toCharArray()) {
			// Get the size for each character and update global image size
			CharInfo charInfo = new CharInfo(width, fontMetrics.charWidth(c) + GraphicalConstants.FONT_WIDTH_PADDING);
			charMap.put(c, charInfo);
			width += charInfo.getWidth();
		}
		graphics.dispose();

		// Check whether there already is a corresponding font texture file and create it if necessary
		String filePath = ResourcePathConstants.FONT_FOLDER + getFontFileName();

		if (! new File(filePath).exists()) {
			// Create the image associated to the charset
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			graphics = image.createGraphics();
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setFont(font);
			fontMetrics = graphics.getFontMetrics();

			graphics.setColor(Color.WHITE);
			for (var entry : charMap.entrySet()) {
				graphics.drawString(String.valueOf(entry.getKey()), entry.getValue().getXPos(), fontMetrics.getAscent());
			}
			graphics.dispose();

			boolean success = ImageIO.write(image, "png", new File(filePath));
			Logger.trace("Saved image " + filePath + " " + success);
		}

		return TextureLoader.loadTexture(filePath);
	}

	private String getAllAvailableChars(String charSetName) {
		CharsetEncoder charsetEncoder = Charset.forName(charSetName).newEncoder();
		StringBuilder result = new StringBuilder();

		for (char c = 0; c < Character.MAX_VALUE; c++) {
			if (charsetEncoder.canEncode(c)) {
				result.append(c);
			}
		}

		return result.toString();
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	private String getFontFileName() {
		if (font == null) return "";
		return 	font.getFontName() + "_" +
				font.getStyle() + "_" +
				font.getSize() + "_" +
				charSetName +
				".png";
	}

	public Texture getTexture() {
		return texture;
	}

	public CharInfo getCharInfo(char c) {
		return charMap.get(c);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
