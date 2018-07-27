package engine.graphics.objects;

import engine.graphics.objects.models.Mesh;
import engine.graphics.objects.textures.CharInfo;
import engine.graphics.objects.textures.FontTexture;
import engine.graphics.objects.textures.Texture;
import engine.utils.converters.FloatConverter;
import engine.utils.converters.IntegerConverter;
import load.TextureLoader;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TextObject extends HUDObject {

	private String text;
	private FontTexture fontTexture;

	private float width, height;

	public TextObject(String text, FontTexture fontTexture) {
		this.text = text;
		this.fontTexture = fontTexture;

		mesh = buildMesh();
	}

	private Mesh buildMesh() {
		char[] characters = text.toCharArray();
		int numberOfCharacters = characters.length;

		List<Float> positions = new ArrayList<>();
		List<Float> textureCoordinates = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		float[] normals = new float[0];

		// please do not confuse with charInfo.xPos
		// this xPos here refers to the current position in the text mesh
		// the charInfo.xPos refers to the position of the char in the font texture
		float xPos = 0f;

		float fontWidth = fontTexture.getTexture().getWidth();
		float fontHeight = fontTexture.getTexture().getHeight();

		for (int i=0; i<numberOfCharacters; i++) {
			CharInfo charInfo = fontTexture.getCharInfo(characters[i]);
			float charWidth = charInfo.getWidth();
			float charXPos = charInfo.getxPos();

			// small tile / quad for current character

			// top left vertex
			positions.add(xPos);
			positions.add(0f);
			positions.add(0f);
			textureCoordinates.add(charXPos / fontWidth);
			textureCoordinates.add(0f);

			// bottom left vertex
			positions.add(xPos);
			positions.add(-fontHeight);
			positions.add(0f);
			textureCoordinates.add(charXPos / fontWidth);
			textureCoordinates.add(1f);

			// top right vertex
			positions.add(xPos + charWidth);
			positions.add(0f);
			positions.add(0f);
			textureCoordinates.add((charXPos + charWidth) / fontWidth);
			textureCoordinates.add(0f);

			// bottom right vertex
			positions.add(xPos + charWidth);
			positions.add(-fontHeight);
			positions.add(0f);
			textureCoordinates.add((charXPos + charWidth) / fontWidth);
			textureCoordinates.add(1f);

			// indices
			indices.add(i*4 + 0);
			indices.add(i*4 + 1);
			indices.add(i*4 + 2);
			indices.add(i*4 + 2);
			indices.add(i*4 + 1);
			indices.add(i*4 + 3);

			xPos += charWidth;
		}

		width = xPos;
		height = fontHeight;

		Mesh mesh = new Mesh(
				FloatConverter.FloatListToFloatArray(positions),
				IntegerConverter.IntegerListToIntArray(indices),
				normals,
				FloatConverter.FloatListToFloatArray(textureCoordinates)
		);
		mesh.normalize();
		mesh.setTexture(fontTexture.getTexture());
		return mesh;
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
		mesh = buildMesh();
	}

	public FontTexture getFontTexture() {
		return fontTexture;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
}
