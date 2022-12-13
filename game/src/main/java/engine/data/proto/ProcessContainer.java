package engine.data.proto;

import constants.PropertyKeys;
import engine.data.variables.DataType;
import engine.data.variables.Variable;

import java.util.Collections;
import java.util.List;

public class ProcessContainer extends Container {

	public ProcessContainer(String textID) {
		super(textID, DataType.PROCESS);
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public List<Container> getSolutions(String stage) {
		return getProperty(stage, PropertyKeys.SOLUTIONS.key()).map(Variable::getContainerList).orElse(Collections.emptyList());
	}
}
