package engine.data.proto;

import constants.ScriptConstants;
import engine.data.identifiers.ContainerIdentifier;
import engine.data.variables.DataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreatureContainer extends Container {

	//private BinaryTree<Process> knownProcesses;

//	private List<ContainerIdentifier> knowledge;
//	private List<ContainerIdentifier> drives;

	public CreatureContainer(String textID) {
		super(textID, DataType.CREATURE);

//		knowledge = new ArrayList<>(4);
//		drives = new ArrayList<>(2);
	}

	// ###################################################################################
	// ################################ Finalization #####################################
	// ###################################################################################

	@Override
	protected void inheritBehaviour(Container container) {
		if (container.getType() == DataType.CREATURE) {
			CreatureContainer creatureContainer = (CreatureContainer) container;

			for (String stage : creatureContainer.getStages()) {
				getOrCreatePropertyList(stage, ScriptConstants.KEY_DRIVES).addAll(creatureContainer.getDrives(stage));
				getOrCreatePropertyList(stage, ScriptConstants.KEY_KNOWLEDGE).addAll(creatureContainer.getKnowledge(stage));
			}
		}
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public List<ContainerIdentifier> getKnowledge(String stage) {
		return getPropertyList(stage, ScriptConstants.KEY_KNOWLEDGE).orElse(Collections.emptyList());
	}

	public List<ContainerIdentifier> getDrives(String stage) {
		return getPropertyList(stage, ScriptConstants.KEY_DRIVES).orElse(Collections.emptyList());
	}
}
