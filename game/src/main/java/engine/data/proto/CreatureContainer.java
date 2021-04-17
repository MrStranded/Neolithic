package engine.data.proto;

import constants.PropertyKeys;
import constants.ScriptConstants;
import engine.data.identifiers.ContainerIdentifier;
import engine.data.variables.DataType;
import engine.data.variables.Variable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
	private List<Container> getKnowledgeStrict(String stage) {
		return getPropertyStrict(stage, PropertyKeys.KNOWLEDGE.key()).map(Variable::getContainerList).orElse(Collections.emptyList());
	}

	public List<Container> getDrives(String stage) {
		return getProperty(stage, PropertyKeys.DRIVES.key()).map(Variable::getContainerList).orElse(Collections.emptyList());
	}
	private List<Container> getDrivesStrict(String stage) {
		return getPropertyStrict(stage, PropertyKeys.DRIVES.key()).map(Variable::getContainerList).orElse(Collections.emptyList());
	}
}
