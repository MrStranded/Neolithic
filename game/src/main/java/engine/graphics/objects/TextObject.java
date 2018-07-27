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

	public TextObject(String text) {
		this.text = text;

		// make this interchangeable?
		this.columns = 16;
		this.rows = 16;

		Texture texture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/font_png.png");
		//Texture texture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/gras.png");
		mesh = buildMesh(texture);

		//setUseDepthTest(false);
	}

	private Mesh buildMesh(Texture texture) {
		byte[] characters = text.getBytes(Charset.forName("ISO-8859-1"));
		int numberOfCharacters = characters.length;

		List<Float> positions = new ArrayList<>();
		List<Float> textureCoordinates = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		float[] normals = new float[0];

		//float tileWidth = (float) texture.getWidth() / (float) columns;
		//float tileHeight = (float) texture.getHeight() / (float) rows;

		float textureWidth = 1f / (float) columns;
		float textureHeight = 1f / (float) rows;

		float fontWidth = 1f / 4f;
		float fontHeight = 1f / 4f;

		for (int i=0; i<numberOfCharacters; i++) {
			int character = characters[i] - 32; // minus 32 because the first 32 chars are control chars that are not in the font texture
			float xPos = (float) (character % columns);
			float yPos = (float) (character / columns);

			// small tile / quad for current character

			// top left vertex
			positions.add(fontWidth * i);
			positions.add(0f);
			positions.add(0f);
			textureCoordinates.add(textureWidth * xPos);
			textureCoordinates.add(textureHeight * yPos);

			// bottom left vertex
			positions.add(fontWidth * i);
			positions.add(-fontHeight);
			positions.add(0f);
			textureCoordinates.add(textureWidth * xPos);
			textureCoordinates.add(textureHeight * (yPos+1));

			// top right vertex
			positions.add(fontWidth * (i+1));
			positions.add(0f);
			positions.add(0f);
			textureCoordinates.add(textureWidth * (xPos+1));
			textureCoordinates.add(textureHeight * yPos);

			// bottom right vertex
			positions.add(fontWidth * (i+1));
			positions.add(-fontHeight);
			positions.add(0f);
			textureCoordinates.add(textureWidth * (xPos+1));
			textureCoordinates.add(textureHeight * (yPos+1));

			// indices
			indices.add(i*4 + 0);
			indices.add(i*4 + 1);
			indices.add(i*4 + 2);
			indices.add(i*4 + 2);
			indices.add(i*4 + 1);
			indices.add(i*4 + 3);
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
