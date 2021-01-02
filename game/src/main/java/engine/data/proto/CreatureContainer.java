package engine.data.proto;

import constants.ScriptConstants;
import engine.data.identifiers.ContainerIdentifier;
import engine.data.variables.DataType;
import org.w3c.dom.ls.LSOutput;

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
				mergePropertyList(stage, ScriptConstants.KEY_DRIVES, creatureContainer.getDrivesStrict(stage));
				mergePropertyList(stage, ScriptConstants.KEY_KNOWLEDGE, creatureContainer.getKnowledgeStrict(stage));
			}
		}
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public List<ContainerIdentifier> getKnowledge(String stage) {
		return getPropertyList(stage, ScriptConstants.KEY_KNOWLEDGE).orElse(Collections.emptyList());
	}
	private List<ContainerIdentifier> getKnowledgeStrict(String stage) {
		return getPropertyListStrict(stage, ScriptConstants.KEY_KNOWLEDGE).orElse(Collections.emptyList());
	}

	public List<ContainerIdentifier> getDrives(String stage) {
		return getPropertyList(stage, ScriptConstants.KEY_DRIVES).orElse(Collections.emptyList());
	}
	private List<ContainerIdentifier> getDrivesStrict(String stage) {
		return getPropertyListStrict(stage, ScriptConstants.KEY_DRIVES).orElse(Collections.emptyList());
	}
}
