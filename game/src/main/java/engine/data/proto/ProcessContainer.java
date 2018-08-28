package engine.data.proto;

import engine.data.ContainerIdentifier;
import engine.data.variables.DataType;

import java.util.ArrayList;
import java.util.List;

public class ProcessContainer extends Container {

	private List<ContainerIdentifier> alternatives;

	public ProcessContainer(String textID) {
		super(textID, DataType.PROCESS);

		alternatives = new ArrayList<>(2);
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public List<ContainerIdentifier> getAlternatives() {
		return alternatives;
	}
}
