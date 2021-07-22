package engine.graphics.objects.gui;

import constants.GraphicalConstants;
import engine.graphics.objects.models.Mesh;
import engine.graphics.objects.textures.CharInfo;
import engine.graphics.objects.textures.FontTexture;
import engine.graphics.renderer.color.RGBA;
import engine.utils.converters.FloatConverter;
import engine.utils.converters.IntegerConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TextObject extends GUIObject {

	private String text;
	private FontTexture fontTexture;
	private RGBA color;
	private double textWidth = 0;

	public TextObject(String text, FontTexture fontTexture) {
		this(text, fontTexture, RGBA.WHITE);
	}
	public TextObject(String text, FontTexture fontTexture, RGBA textColor) {
		this.text = text;
		this.fontTexture = fontTexture;
		this.color = textColor;

		setMesh(buildMesh());
	}

	private Mesh buildMesh() {
		char[] characters = text.toCharArray();
		int numberOfCharacters = characters.length;

		List<Integer> indices = new ArrayList<>();
		List<Float> positions = new ArrayList<>();
		List<Float> textureCoordinates = new ArrayList<>();
		List<Float> colors = new ArrayList<>();
		float[] normals = new float[0];

		// please do not confuse with charInfo.xPos
		// this xPos here refers to the current position in the text mesh
		// the charInfo.xPos refers to the position of the char in the font texture
		double xPos = 0f;

		double fontWidth = fontTexture.getTexture().getWidth();
		float fontHeight = fontTexture.getTexture().getHeight();
		if (fontHeight == 0) { fontHeight = 1; }

		for (int i=0; i<numberOfCharacters; i++) {
			CharInfo charInfo = fontTexture.getCharInfo(characters[i]);
			double charWidth = charInfo.getWidth();
			double charXPos = charInfo.getXPos();

			// small tile / quad for current character

			// top left vertex
			positions.add((float) xPos);
			positions.add(1f);
			positions.add(0f);
			textureCoordinates.add((float) (charXPos / fontWidth));
			textureCoordinates.add(0f);
			addColor(colors);

			// bottom left vertex
			positions.add((float) xPos);
			positions.add(0f);
			positions.add(0f);
			textureCoordinates.add((float) (charXPos / fontWidth));
			textureCoordinates.add(1f);
			addColor(colors);

			// top right vertex
			positions.add((float) (xPos + charWidth));
			positions.add(1f);
			positions.add(0f);
			textureCoordinates.add((float) ((charXPos + charWidth - GraphicalConstants.FONT_WIDTH_PADDING) / fontWidth));
			textureCoordinates.add(0f);
			addColor(colors);

			// bottom right vertex
			positions.add((float) (xPos + charWidth));
			positions.add(0f);
			positions.add(0f);
			textureCoordinates.add((float) ((charXPos + charWidth - GraphicalConstants.FONT_WIDTH_PADDING) / fontWidth));
			textureCoordinates.add(1f);
			addColor(colors);

			// indices
			indices.add(i*4 + 0);
			indices.add(i*4 + 1);
			indices.add(i*4 + 2);
			indices.add(i*4 + 2);
			indices.add(i*4 + 1);
			indices.add(i*4 + 3);

			xPos += charWidth;
		}
		textWidth = xPos / fontHeight;

		// normalizing mesh (only x axis necessary, y axis already normal)
		for (int i = 0; i < positions.size(); i += 3) {
			positions.set(i, (float) (positions.get(i) / xPos));
		}

		// MICHASCHLEGDPIPIBTMELONNICE
		Mesh mesh = new Mesh(
                IntegerConverter.IntegerListToIntArray(indices),
				FloatConverter.FloatListToFloatArray(positions),
				FloatConverter.FloatListToFloatArray(textureCoordinates),
				FloatConverter.FloatListToFloatArray(colors),
				normals
		);

		mesh.getMaterial().setTexture(fontTexture.getTexture());
		return mesh;
	}

	private void addColor(List<Float> colors) {
		colors.add((float) color.getR());
		colors.add((float) color.getG());
		colors.add((float) color.getB());
		colors.add((float) color.getA());
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		mesh.cleanUp();
		setMesh(buildMesh());
	}

	/**
	 * When we define the height of the text mesh as 1, then the textWidth is the width of the mesh in relation to its height.
	 * @return width of text mesh
	 */
	public double getTextWidth() {
		return textWidth;
	}

	public FontTexture getFontTexture() {
		return fontTexture;
	}
}
