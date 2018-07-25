package engine.graphics.objects;

import engine.graphics.objects.models.Mesh;
import engine.graphics.objects.models.Texture;
import engine.utils.converters.FloatConverter;
import engine.utils.converters.IntegerConverter;
import load.TextureLoader;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TextObject extends GraphicalObject {

	private String text;
	private int columns, rows;

	public TextObject(String text, int columns, int rows) {
		this.text = text;
		this.columns = columns;
		this.rows = rows;

		Texture texture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/font_png.png");
		mesh = buildMesh(texture);
	}

	private Mesh buildMesh(Texture texture) {
		byte[] characters = text.getBytes(Charset.forName("ISO-8859-1"));
		int numberOfCharacters = characters.length;

		List<Float> positions = new ArrayList<>();
		List<Float> textureCoordinates = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		float[] normals = new float[0];

		float tileWidth = (float) texture.getWidth() / (float) columns;
		float tileHeight = (float) texture.getHeight() / (float) rows;

		for (int i=0; i<numberOfCharacters; i++) {
			byte character = characters[i];
			int xPos = character % columns;
			int yPos = character / columns;

			// small tile / quad for current character

			// top left vertex
			positions.add(tileWidth * i);
			positions.add(0f);
			positions.add(0f);
			textureCoordinates.add(tileWidth * xPos);
			textureCoordinates.add(tileHeight * yPos);

			// bottom left vertex
			positions.add(tileWidth * i);
			positions.add(-tileHeight);
			positions.add(0f);
			textureCoordinates.add(tileWidth * xPos);
			textureCoordinates.add(tileHeight * (yPos+1));

			// top right vertex
			positions.add(tileWidth * (i+1));
			positions.add(0f);
			positions.add(0f);
			textureCoordinates.add(tileWidth * (xPos+1));
			textureCoordinates.add(tileHeight * yPos);

			// bottom right vertex
			positions.add(tileWidth * (i+1));
			positions.add(-tileHeight);
			positions.add(0f);
			textureCoordinates.add(tileWidth * (xPos+1));
			textureCoordinates.add(tileHeight * (yPos+1));

			// indices
			indices.add(i*4 + 0);
			indices.add(i*4 + 1);
			indices.add(i*4 + 2);
			indices.add(i*4 + 2);
			indices.add(i*4 + 3);
			indices.add(i*4 + 0);
		}

		Mesh mesh = new Mesh(
				FloatConverter.FloatListToFloatArray(positions),
				IntegerConverter.IntegerListToIntArray(indices),
				normals,
				FloatConverter.FloatListToFloatArray(textureCoordinates)
		);
		mesh.setTexture(texture);
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
		Texture texture = mesh.getTexture();
		mesh.cleanUp();
		mesh = buildMesh(texture);
	}
}
