package engine.data.proto;

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
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public List<ContainerIdentifier> getKnowledge() {
		return knowledge;
	}

	public List<ContainerIdentifier> getDrives() {
		return drives;
	}
}
