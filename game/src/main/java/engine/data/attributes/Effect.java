package engine.data.attributes;

import engine.data.IDInterface;
import engine.data.structures.BinaryTree;
import engine.data.time.GameTime;

public class Effect implements IDInterface {

	private int id;
	private GameTime creation; // the creation time is necessary - can never be null
	private GameTime duration = null; // when duration is null, this means that this effect does not expire
	private BinaryTree<Attribute> attributes;

	public Effect(int id, GameTime creation) {
		this.id = id;
		creation = new GameTime(creation);
		attributes = new BinaryTree<>();
	}

	public void add(Attribute attribute) {
		attributes.insert(attribute);
	}

	public Attribute get(int id) {
		return attributes.get(id);
	}

	public int getValue(int id) {
		Attribute attribute = attributes.get(id);
		return attribute != null? attribute.getValue() : 0;
	}

	public boolean hasExpired(GameTime time) {
		if (duration == null) {
			return false;
		}

		return creation.getMilliseconds() + duration.getMilliseconds() > time.getMilliseconds();
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public void setEternal() {
		duration = null;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public IDInterface merge(IDInterface other) {
		attributes.insert(((Effect) other).attributes);
		return this;
	}
}
