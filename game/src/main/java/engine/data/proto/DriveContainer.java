package engine.data.proto;

import constants.ScriptConstants;
import engine.data.identifiers.ContainerIdentifier;
import engine.data.variables.DataType;

import java.util.ArrayList;
import java.util.List;

public class DriveContainer extends Container {

//	private List<ContainerIdentifier> solutions;

	public DriveContainer(String textID) {
		super(textID, DataType.DRIVE);

//		solutions = new ArrayList<>(2);
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public List<ContainerIdentifier> getSolutions() {
//		return solutions;
		return getDefaultStage().getIdList(ScriptConstants.KEY_SOLUTIONS);
	}
}
