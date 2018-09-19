package engine.data.proto;

import engine.data.identifiers.ContainerIdentifier;
import engine.data.variables.DataType;

import java.util.ArrayList;
import java.util.List;

public class ProcessContainer extends Container {

	private List<ContainerIdentifier> solutions;

	public ProcessContainer(String textID) {
		super(textID, DataType.PROCESS);

		solutions = new ArrayList<>(2);
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public List<ContainerIdentifier> getSolutions() {
		return solutions;
	}
}
