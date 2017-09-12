package data;

import environment.world.Entity;
import environment.world.Planet;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * This class holds all the proto entities of the game, which in turn store all the scripts.
 *
 * Created by Michael on 05.09.2017.
 */
public class Data {

	private static ConcurrentLinkedDeque<Entity> protoEntities = new ConcurrentLinkedDeque<Entity>();
	private static Planet planet = null;

	// ###################################################################################
	// ################################ Modification #####################################
	// ###################################################################################

	public static boolean hasEntity(String textID) {
		for (Entity entity : protoEntities) {
			if (entity.getTextID().equals(textID)) return true;
		}
		return false;
	}

	public static Entity getOrCreateEntity(String textID) {
		for (Entity entity : protoEntities) {
			if (entity.getTextID().equals(textID)) return entity;
		}
		Entity entity = new Entity(textID);
		protoEntities.add(entity);
		return entity;
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################

	public static ConcurrentLinkedDeque<Entity> getProtoEntities() {
		return protoEntities;
	}
	public static void setProtoEntities(ConcurrentLinkedDeque<Entity> protoEntities) {
		Data.protoEntities = protoEntities;
	}

	public static Planet getPlanet() {
		return planet;
	}
	public static void setPlanet(Planet planet) {
		Data.planet = planet;
	}
}
