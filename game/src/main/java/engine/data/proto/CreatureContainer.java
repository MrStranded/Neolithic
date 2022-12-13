package engine.data.proto;

import constants.PropertyKeys;
import engine.data.variables.DataType;
import engine.data.variables.Variable;

import java.util.Collections;
import java.util.List;

public class CreatureContainer extends Container {

	public CreatureContainer(String textID) {
		super(textID, DataType.CREATURE);
	}

	// ###################################################################################
	// ################################ Finalization #####################################
	// ###################################################################################

	@Override
	protected void inheritBehaviour(Container container) {
		if (container.getType() == DataType.CREATURE) {
			CreatureContainer creatureContainer = (CreatureContainer) container;

			for (String stage : creatureContainer.getStages()) {
				mergePropertyList(stage, PropertyKeys.DRIVES.key(), creatureContainer);
				mergePropertyList(stage, PropertyKeys.KNOWLEDGE.key(), creatureContainer);
			}
		}
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public List<Container> getKnowledge(String stage) {
		return getProperty(stage, PropertyKeys.KNOWLEDGE.key()).map(Variable::getContainerList).orElse(Collections.emptyList());
	}

	public List<Container> getDrives(String stage) {
		return getProperty(stage, PropertyKeys.DRIVES.key()).map(Variable::getContainerList).orElse(Collections.emptyList());
	}
}
