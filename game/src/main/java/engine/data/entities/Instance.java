package engine.data.entities;

import engine.data.attributes.Effect;
import engine.data.proto.Container;
import engine.data.proto.Data;
import engine.graphics.objects.movement.MoveableObject;

import java.util.ArrayList;
import java.util.List;

public class Instance {

	private int id;

	List<Effect> effects;
	List<Instance> subInstances;

	public Instance(int id) {
		effects = new ArrayList<>(4);
		subInstances = new ArrayList<>(4);
	}

	// ###################################################################################
	// ################################ Graphical ########################################
	// ###################################################################################

	// ###################################################################################
	// ################################ Sub Instances ####################################
	// ###################################################################################

	public boolean contains(int subId) {
		for (Instance instance : subInstances) {
			if (instance.getId() == subId) {
				return true;
			}
		}
		return false;
	}

	// ###################################################################################
	// ################################ Effects ##########################################
	// ###################################################################################

	public void addEffect(Effect effect) {
		// maybe we have to merge two effects
		boolean exists = false;
		for (Effect currentEffect : effects) {
			if (currentEffect != null) {
				if (currentEffect.getId() == effect.getId()) {
					exists = true;
					currentEffect.merge(effect);
					break;
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

	public int getId() {
		return id;
	}
}
