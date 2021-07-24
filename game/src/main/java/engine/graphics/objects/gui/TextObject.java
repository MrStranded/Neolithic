package engine.graphics.objects.gui;

import constants.GraphicalConstants;
import engine.graphics.objects.models.Mesh;
import engine.graphics.objects.textures.CharInfo;
import engine.graphics.objects.textures.FontTexture;
import engine.graphics.renderer.color.RGBA;
import engine.math.numericalObjects.Vector2;
import engine.utils.converters.FloatConverter;
import engine.utils.converters.IntegerConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TextObject extends GUIObject {

	private String text;
	private FontTexture fontTexture;
	private RGBA color;
	private double textWidth = 0d, textHeight = 1d;
	private double maxTextWidth;

	public TextObject(String text, FontTexture fontTexture) {
		this(text, fontTexture, RGBA.WHITE);
	}
	public TextObject(String text, FontTexture fontTexture, RGBA textColor) {
		this(text, fontTexture, textColor, 0);
	}
	public TextObject(String text, FontTexture fontTexture, RGBA textColor, double maxTextWidth) {
		this.text = text;
		this.fontTexture = fontTexture;
		this.color = textColor;
		this.maxTextWidth = maxTextWidth;

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

		double largestWidth = 0d;

		double fontWidth = fontTexture.getTexture().getWidth();
		float fontHeight = fontTexture.getTexture().getHeight();
		if (fontHeight == 0) { fontHeight = 1; }

		// position of the mesh quad cursor
		Vector2 pos = new Vector2(0, 0);

		for (int i=0; i<numberOfCharacters; i++) {
			CharInfo charInfo = fontTexture.getCharInfo(characters[i]);
			double charWidth = charInfo.getWidth();
			double charXPos = charInfo.getXPos();

			// line break
			if (characters[i] == 10) {
				setCursorToNewLine(pos, fontHeight);
				continue;
			} else if (maxTextWidth > 0 && pos.getX() > 0 && pos.getX() + charWidth > maxTextWidth) {
				setCursorToNewLine(pos, fontHeight);
			}

			// small tile / quad for current character

			// top left vertex
			positions.add((float) pos.getX());
			positions.add((float) pos.getY());
			positions.add(0f);
			textureCoordinates.add((float) (charXPos / fontWidth));
			textureCoordinates.add(0f);
			addColor(colors);

			// bottom left vertex
			positions.add((float) pos.getX());
			positions.add((float) pos.getY() - fontHeight);
			positions.add(0f);
			textureCoordinates.add((float) (charXPos / fontWidth));
			textureCoordinates.add(1f);
			addColor(colors);

			// top right vertex
			positions.add((float) (pos.getX() + charWidth));
			positions.add((float) pos.getY());
			positions.add(0f);
			textureCoordinates.add((float) ((charXPos + charWidth - GraphicalConstants.FONT_WIDTH_PADDING) / fontWidth));
			textureCoordinates.add(0f);
			addColor(colors);

			// bottom right vertex
			positions.add((float) (pos.getX() + charWidth));
			positions.add((float) pos.getY() - fontHeight);
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

			pos.setX(pos.getX() + charWidth);
			largestWidth = Math.max(largestWidth, pos.getX());
		}

		textWidth = largestWidth / fontHeight;

		// normalizing mesh
		if (largestWidth > 0) {
			double fullHeight = fontHeight * textHeight;
			for (int i = 0; i < positions.size(); i ++) {
				if (i % 3 == 0) { positions.set(i, (float) (positions.get(i) / largestWidth)); }
				if (i % 3 == 1) { positions.set(i, (float) ((positions.get(i) + fullHeight) / fullHeight)); }
			}
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

	private void setCursorToNewLine(Vector2 cursor, double lineHeight) {
		cursor.setX(0d);
		cursor.setY(cursor.getY() - lineHeight);
		textHeight++;
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
	 * When we define the height of one line of the text mesh as 1, then the textWidth is the width of the mesh in relation to one line.
	 * @return width of text mesh
	 */
	public double getTextWidth() {
		return textWidth;
	}
	/**
	 * Returns the number of lines of the text
	 * @return height of text mesh
	 */
	public double getTextHeight() {
		return textHeight;
	}

	public FontTexture getFontTexture() {
		return fontTexture;
	}
}
