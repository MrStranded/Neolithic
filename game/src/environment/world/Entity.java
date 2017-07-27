package environment.world;

/**
 * Entities cover almost everything from items to units to objects.
 * Thus, they are rather abstract and should be kept flexible.
 *
 * Created by Michael on 27.07.2017.
 */
public class Entity {

	private char thumbnail;
	private Tile tile;

	public Entity (Tile tile, char thumbnail) {
		this.tile = tile;
		this.thumbnail = thumbnail;
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################

	public Tile getTile() {
		return tile;
	}
	public void setTile(Tile tile) {
		this.tile = tile;
	}

	public char getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(char thumbnail) {
		this.thumbnail = thumbnail;
	}
}
