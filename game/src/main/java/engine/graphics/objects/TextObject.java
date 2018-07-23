package engine.graphics.objects;

import engine.graphics.objects.models.Mesh;
import engine.graphics.objects.models.Texture;
import load.TextureLoader;

public class TextObject extends GraphicalObject {

	private String text;

	public TextObject(String text) {
		this.text = text;
		Texture texture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/font_png.png");


	}
}
