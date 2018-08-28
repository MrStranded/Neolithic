package engine.data.proto;

import engine.data.variables.DataType;

import java.util.ArrayList;
import java.util.List;

public class ProcessContainer extends Container {

	private List<String> preAlternatives;

	public ProcessContainer(String textID) {
		super(textID, DataType.PROCESS);

		preAlternatives = new ArrayList<>(2);
	}

	// ###################################################################################
	// ################################ Preparing for Game ###############################
	// ###################################################################################

	public void finalizeAlternatives() {
		// convert preSolutions into solutions binary tree
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public void addAlternative(String textID) {
		if (textID != null) {
			preAlternatives.add(textID);
		}
	}

	public List<String> getPreAlternatives() {
		return preAlternatives;
	}
}
