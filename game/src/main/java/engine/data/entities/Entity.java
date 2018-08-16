package engine.data.entities;

import engine.data.attributes.Effect;
import engine.data.proto.Container;
import engine.data.proto.Data;

import java.util.ArrayList;
import java.util.List;

public class Entity {

	private int id;
	private String personalName = null;

	List<Effect> effects;

	public Entity(int id) {
		effects = new ArrayList<>(4);
	}

	public void addEffect(Effect effect) {
		// maybe we have to merge two effects
		boolean exists = false;
		for (Effect currentEffect : effects) {
			if (currentEffect != null) {
				if (currentEffect.getId() == effect.getId()) {
					exists = true;
					currentEffect.merge(effect);
				}
			}
		}

		// no merge was necessary -> add as usual
		if (!exists) {
			effects.add(effect);
		}
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public int getAttribute(int attributeID) {
		int value = 0;

		// general data
		Container container = Data.get(id);
		value += container != null? container.getAttribute(attributeID) : 0;

		// personal data
		for (Effect effect : effects) {
			value += effect != null? effect.getValue(attributeID) : 0;
		}

		return value;
	}
}
