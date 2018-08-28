package engine.data.proto;

import engine.data.variables.DataType;

import java.util.ArrayList;
import java.util.List;

public class DriveContainer extends Container {

	private List<String> preSolutions;

	public DriveContainer(String textID) {
		super(textID, DataType.DRIVE);

		preSolutions = new ArrayList<>(2);
	}

	// ###################################################################################
	// ################################ Preparing for Game ###############################
	// ###################################################################################

	public void finalizeSolutions() {
		// convert preSolutions into solutions binary tree
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public void addSolution(String textID) {
		if (textID != null) {
			preSolutions.add(textID);
		}
	}

	public List<String> getPreSolutions() {
		return preSolutions;
	}
}
