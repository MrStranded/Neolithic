package engine.data.proto;

import engine.data.structures.trees.binary.BinaryTree;
import engine.data.variables.DataType;

import java.util.ArrayList;
import java.util.List;

public class CreatureContainer extends Container {

	//private BinaryTree<Process> knownProcesses;

	private List<String> preKnownProcesses;

	public CreatureContainer(String textID) {
		super(textID, DataType.ENTITY);

		preKnownProcesses = new ArrayList<>(4);
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

	/**
	 * For Debugging only! Remove soon!
	 * @return
	 */
	public List<String> getPreKnownProcesses() {
		return preKnownProcesses;
	}
}
