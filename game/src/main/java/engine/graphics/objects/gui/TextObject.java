package engine.graphics.objects.gui;

import engine.graphics.objects.models.Mesh;
import engine.graphics.objects.textures.CharInfo;
import engine.graphics.objects.textures.FontTexture;
import engine.utils.converters.FloatConverter;
import engine.utils.converters.IntegerConverter;

import java.util.ArrayList;
import java.util.List;

public class TextObject extends GUIObject {

	private String text;
	private FontTexture fontTexture;

	public TextObject(String text, FontTexture fontTexture) {
		this.text = text;
		this.fontTexture = fontTexture;

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

		for (int i=0; i<numberOfCharacters; i++) {
			CharInfo charInfo = fontTexture.getCharInfo(characters[i]);
			double charWidth = charInfo.getWidth();
			double charXPos = charInfo.getxPos();

			// small tile / quad for current character

			// top left vertex
			positions.add((float) xPos);
			positions.add(0f);
			positions.add(0f);
			textureCoordinates.add((float) (charXPos / fontWidth));
			textureCoordinates.add(0f);
			for (int c=0; c<4; c++) { colors.add(1f); }

			// bottom left vertex
			positions.add((float) xPos);
			positions.add(-fontHeight);
			positions.add(0f);
			textureCoordinates.add((float) (charXPos / fontWidth));
			textureCoordinates.add(1f);
			for (int c=0; c<4; c++) { colors.add(1f); }

			// top right vertex
			positions.add((float) (xPos + charWidth));
			positions.add(0f);
			positions.add(0f);
			textureCoordinates.add((float) ((charXPos + charWidth) / fontWidth));
			textureCoordinates.add(0f);
			for (int c=0; c<4; c++) { colors.add(1f); }

			// bottom right vertex
			positions.add((float) (xPos + charWidth));
			positions.add(-fontHeight);
			positions.add(0f);
			textureCoordinates.add((float) ((charXPos + charWidth) / fontWidth));
			textureCoordinates.add(1f);
			for (int c=0; c<4; c++) { colors.add(1f); }

			// indices
			indices.add(i*4 + 0);
			indices.add(i*4 + 1);
			indices.add(i*4 + 2);
			indices.add(i*4 + 2);
			indices.add(i*4 + 1);
			indices.add(i*4 + 3);

			xPos += charWidth;
		}

		// normalizing mesh
		for (int i = 0; i < positions.size(); i++) {
			if (i % 3 == 0) {
				positions.set(i, (float) (positions.get(i) / xPos));
			} else if (i % 3 == 1) {
				positions.set(i, positions.get(i) / fontHeight + 1f);
			}
		}
//MICHASCHLEGDPIPIBTMELONNICE
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

	public FontTexture getFontTexture() {
		return fontTexture;
	}
}
