package engine.data.proto;

import constants.ScriptConstants;
import engine.data.identifiers.ContainerIdentifier;
import engine.data.variables.DataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DriveContainer extends Container {

	public DriveContainer(String textID) {
		super(textID, DataType.DRIVE);
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public List<ContainerIdentifier> getSolutions(String stage) {
		return getPropertyList(stage, ScriptConstants.KEY_SOLUTIONS).orElse(Collections.emptyList());
	}
}
