package data.proto;

import data.personal.Attribute;
import enums.script.ObjectType;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by Michael on 26.09.2017.
 *
 * Containers are used for most game objects. They store the very most general kind of data about the objects.
 */
public class Container {

	private ObjectType type = ObjectType.NONE;
	private String textId = "";
	private int id = 0;

	private ConcurrentLinkedDeque<Value> values = new ConcurrentLinkedDeque<>();
	private ConcurrentLinkedDeque<Attribute> attributes = new ConcurrentLinkedDeque<>();

	public Container(String objectType) {
		type = ObjectType.OBJECT.get(objectType);
	}

	// ###################################################################################
	// ################################ Modification ###### Values #######################
	// ###################################################################################

	public void addValue(Value value) {
		values.add(value);
	}

	public Value tryToGet(String name) {
		if (name != null) {
			for (Value value : values) {
				if (name.equals(value.getName())) return value;
			}
		}
		return null;
	}

	public String getString(String name) {
		return getString(name,0);
	}
	public String getString(String name, int i) {
		Value value = tryToGet(name);
		if (value != null) {
			String v = value.tryToGetString(i);
			if (v != null) return v;
		}
		return "";
	}

	public int getInt(String name) {
		return getInt(name,0);
	}
	public int getInt(String name, int i) {
		Value value = tryToGet(name);
		if (value != null) {
			return value.tryToGetInt(i);
		}
		return 0;
	}

	// ###################################################################################
	// ################################ Modification ###### Attributes ###################
	// ###################################################################################

	public void addAttribute(Attribute attribute) {
		for (Attribute a : attributes) {
			if (a.getId() == attribute.getId()) {
				a.setValue(a.getValue() + attribute.getValue());
				return;
			}
		}
		attributes.add(attribute);
	}
	public void addAttribute(int id,int value) {
		addAttribute(new Attribute(id,value));
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################

	public ObjectType getType() {
		return type;
	}

	public String getTextId() {
		return textId;
	}
	public void setTextId(String textId) {
		this.textId = textId;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public ConcurrentLinkedDeque<Value> getValues() {
		return values;
	}
	public void setValues(ConcurrentLinkedDeque<Value> values) {
		this.values = values;
	}

	public ConcurrentLinkedDeque<Attribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(ConcurrentLinkedDeque<Attribute> attributes) {
		this.attributes = attributes;
	}
}
