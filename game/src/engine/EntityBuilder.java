package engine;

import data.Data;
import data.personal.Attribute;
import data.proto.Container;
import data.proto.ProtoAttribute;
import environment.world.Entity;
import environment.world.Tile;

/**
 * Created by michael1337 on 26/10/17.
 */
public class EntityBuilder {

	/**
	 * When possible, the method createEntity(Tile, int) should be preferred for speed reasons.
	 */
	public static Entity createEntity(Tile tile, String textId) {
		return createEntity(tile,Data.getContainerId(textId));
	}

	public static Entity createEntity(Tile tile, int id) {
		Entity entity = new Entity();
		buildEntityFromContainer(entity,Data.getContainer(id));

		entity.setTile(tile);
		tile.addEntity(entity);
		Data.getPlanet().signEntity(entity);
		return entity;
	}

	public static void setTileEntity(Tile tile, int id) {
		Entity entity = new Entity();
		buildEntityFromContainer(entity,Data.getContainer(id));

		entity.setTile(tile);
		tile.setSelf(entity);
		// here we could sign the entity in into the entity processing thread if wanted
	}

	private static void buildEntityFromContainer(Entity entity, Container container) {
		if (container != null) {
			entity.setId(container.getId());
			entity.setTextID(container.getTextId());

			String charString = container.getString("char");
			if ((charString != null) && (charString.length() > 0)) {
				entity.setThumbnail(charString.charAt(0));
			} else {
				entity.setThumbnail('!');
			}

			EntityBuilder.addProtoAttributesToEntity(entity);
		} else {
			entity.setThumbnail('?');
		}
	}

	private static void addProtoAttributesToEntity(Entity entity) {
		if (entity == null) return;

		Container container = Data.getContainer(entity.getId());
		if (container != null) {
			for (Attribute attribute : container.getAttributes()) {
				entity.addAttribute(new Attribute(attribute));
			}
		}
	}

}
