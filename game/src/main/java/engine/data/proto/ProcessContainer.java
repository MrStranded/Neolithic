package engine.data.proto;

import constants.ScriptConstants;
import engine.data.identifiers.ContainerIdentifier;
import engine.data.variables.DataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProcessContainer extends Container {

	public ProcessContainer(String textID) {
		super(textID, DataType.PROCESS);
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public List<ContainerIdentifier> getSolutions(String stage) {
		return getPropertyList(stage, ScriptConstants.KEY_SOLUTIONS).orElse(Collections.emptyList());
	}
}
