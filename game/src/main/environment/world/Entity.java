package main.environment.world;

import main.data.personal.Attribute;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Entities cover almost everything from items to units to objects.
 * Thus, they are rather abstract and should be kept flexible.
 *
 * Created by Michael on 27.07.2017.
 */
public class Entity {

	private String textID = "";
	private int id = 0;
	private char thumbnail = 'X';
	private Tile tile = null;
	private int amount = 1;

	private ConcurrentLinkedDeque<Entity> entities = new ConcurrentLinkedDeque<Entity>(); // contained entities
	private ConcurrentLinkedDeque<Attribute> attributes = new ConcurrentLinkedDeque<Attribute>();

	/**
	 * Entities are filled with data by the EntityBuilder and should also only be created there.
	 */
	public Entity() {}

	// ###################################################################################
	// ################################ Modification #####################################
	// ###################################################################################

	public void addEntity (Entity entity) {
		entities.add(entity);
	}

	public void addAttribute(Attribute attribute) {
		attributes.add(attribute);
	}

	public int getAttribute(int id) {
		int a = 0;
		for (Attribute attribute : attributes) {
			if (attribute.getId() == id) a += attribute.getValue();
		}
		return a;
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################


	public ConcurrentLinkedDeque<Entity> getEntities() {
		return entities;
	}

	public String getTextID() {
		return textID;
	}
	public void setTextID(String textID) {
		this.textID = textID;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

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

	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
}
