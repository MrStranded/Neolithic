package data;

import data.proto.ProtoAttribute;
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

	private static ConcurrentLinkedDeque<ProtoAttribute> protoAttributes = new ConcurrentLinkedDeque<>();
	private static Planet planet = null;

	// ###################################################################################
	// ################################ Modification #####################################
	// ###################################################################################

	public static void addProtoAttribute(ProtoAttribute protoAttribute) {
		int id = protoAttributes.size();
		protoAttribute.setId(id);
		protoAttributes.add(protoAttribute);
	}

	public static ProtoAttribute getProtoAttribute(int id) {
		for (ProtoAttribute protoAttribute : protoAttributes) {
			if (protoAttribute.getId() == id) return protoAttribute;
		}
		return null;
	}

	public static int getProtoAttributeId(String textId) {
		for (ProtoAttribute protoAttribute : protoAttributes) {
			if (textId.equals(protoAttribute.getTextId())) return protoAttribute.getId();
		}
		return -1;
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################

	public static Planet getPlanet() {
		return planet;
	}
	public static void setPlanet(Planet planet) {
		Data.planet = planet;
	}
}
