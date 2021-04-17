package engine.data.proto;

import constants.PropertyKeys;
import constants.ScriptConstants;
import engine.data.identifiers.ContainerIdentifier;
import engine.data.variables.DataType;
import engine.data.variables.Variable;

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

	public List<Container> getSolutions(String stage) {
		return getProperty(stage, PropertyKeys.SOLUTIONS.key()).map(Variable::getContainerList).orElse(Collections.emptyList());
	}
}
