package engine.data.proto;

import engine.data.structures.trees.binary.BinaryTree;
import engine.data.variables.DataType;

import java.util.ArrayList;
import java.util.List;

public class CreatureContainer extends Container {

	//private BinaryTree<Process> knownProcesses;

	private List<String> preKnownProcesses;
	private List<String> preDrives;

	public CreatureContainer(String textID) {
		super(textID, DataType.CREATURE);

		preKnownProcesses = new ArrayList<>(4);
		preDrives = new ArrayList<>(2);
	}

	// ###################################################################################
	// ################################ Preparing for Game ###############################
	// ###################################################################################

	public void finalizeBehaviour() {
		// convert preKnownProcesses into processes binary tree
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public void addPreKnownProcess(String textID) {
		if (textID != null) {
			preKnownProcesses.add(textID);
		}
	}

	public void addPreDrive(String textID) {
		if (textID != null) {
			preDrives.add(textID);
		}
	}

	public List<String> getPreKnownProcesses() {
		return preKnownProcesses;
	}

	public List<String> getPreDrives() {
		return preDrives;
	}
}
