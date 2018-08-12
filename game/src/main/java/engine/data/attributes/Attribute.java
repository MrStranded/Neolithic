package engine.data.attributes;

import engine.data.IDInterface;

public class Attribute implements IDInterface {

	private int id;

	public Attribute(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public IDInterface merge(IDInterface other) {
		return this;
	}
}
