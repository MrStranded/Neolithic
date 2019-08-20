package engine.data.proto;

import engine.data.IDInterface;
import engine.data.Script;
import engine.data.attributes.Attribute;
import engine.data.identifiers.ContainerIdentifier;
import engine.data.variables.DataType;

import java.util.ArrayList;
import java.util.List;

public class CreatureContainer extends Container {

	//private BinaryTree<Process> knownProcesses;

	private List<ContainerIdentifier> knowledge;
	private List<ContainerIdentifier> drives;

	public CreatureContainer(String textID) {
		super(textID, DataType.CREATURE);

		knowledge = new ArrayList<>(4);
		drives = new ArrayList<>(2);
	}

	// ###################################################################################
	// ################################ Finalization #####################################
	// ###################################################################################

	@Override
	protected void inheritBehaviour(Container container) {
		if (container.getType() == DataType.CREATURE) {
			CreatureContainer creatureContainer = (CreatureContainer) container;

			drives.addAll(creatureContainer.getDrives());
			knowledge.addAll(creatureContainer.getKnowledge());
		}
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public List<ContainerIdentifier> getKnowledge() {
		return knowledge;
	}

	public List<ContainerIdentifier> getDrives() {
		return drives;
	}
}
