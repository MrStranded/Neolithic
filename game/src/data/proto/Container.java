package data.proto;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by Michael on 26.09.2017.
 *
 * Containers are used for most game objects. They store the very most general kind of data about the objects.
 */
public class Container {

	private String type = "";
	private String textId = "";
	private int id = 0;

	private ConcurrentLinkedDeque<Value> values = new ConcurrentLinkedDeque<>();

	public Container(String type) {
		this.type = type;
	}

	// ###################################################################################
	// ################################ Modification #####################################
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

	public String getString(String name, int i) {
		Value value = tryToGet(name);
		if (value != null) {
			String v = value.tryToGetString(i);
			if (v != null) return v;
		}
		return "";
	}
	public int getInt(String name, int i) {
		Value value = tryToGet(name);
		if (value != null) {
			return value.tryToGetInt(i);
		}
		return 0;
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
}
