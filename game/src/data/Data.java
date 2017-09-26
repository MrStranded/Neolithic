package data;

import data.personal.Attribute;
import data.proto.Container;
import data.proto.ProtoAttribute;
import data.proto.Value;
import enums.script.ContainerValue;
import enums.script.ObjectType;
import environment.world.Entity;
import environment.world.Planet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * This class holds all the proto entities of the game, which in turn store all the scripts.
 *
 * Created by Michael on 05.09.2017.
 */
public class Data {

	private static ConcurrentLinkedDeque<ProtoAttribute> protoAttributes = new ConcurrentLinkedDeque<>();
	private static ConcurrentLinkedDeque<Container> containers = new ConcurrentLinkedDeque<>();
	private static Planet planet = null;

	// ###################################################################################
	// ################################ Modification ###### Attributes ###################
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
	// ################################ Modification ###### Containers ###################
	// ###################################################################################

	public static void addContainer(Container container) {
		if (container.getType() == ObjectType.ATTRIBUTE) {  // Attributes are handled separately
			String name = container.getString(ContainerValue.NAME.toString());
			boolean flag = (container.getInt(ContainerValue.FLAG.toString()) != 0);
			boolean mutation = (container.getInt(ContainerValue.MUTATION.toString()) != 0);
			ProtoAttribute protoAttribute = new ProtoAttribute(name,container.getTextId(),flag,mutation);
			addProtoAttribute(protoAttribute);
		} else {                                            // all other kinds of objects
			int id = containers.size();
			container.setId(id);
			containers.add(container);
		}
	}

	public static void turnValuesIntoAttributes() {
		for (Container container : containers) {
			if (container.getValues().size() == 0) return;
			Iterator<Value> iterator = container.getValues().iterator();
			while (iterator.hasNext()) {
				Value value = iterator.next();
				if (ContainerValue.DNA.equals(value.getName())) {
					String textId = value.tryToGetString(0);
					int val = value.tryToGetInt(1);
					container.addAttribute(getProtoAttributeId(textId),val);
					iterator.remove();
				}
			}
		}
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

	public static ConcurrentLinkedDeque<ProtoAttribute> getProtoAttributes() {
		return protoAttributes;
	}

	public static ConcurrentLinkedDeque<Container> getContainers() {
		return containers;
	}
}
