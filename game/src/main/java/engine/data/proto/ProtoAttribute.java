package engine.data.proto;

public class ProtoAttribute {

	private String textID;
	private String name = "Unnamed attribute";

	private boolean inherited = false;
	private double mutationChance = 0;
	private double mutationExtent = 0;

	public ProtoAttribute(String textID) {
		this.textID = textID;
	}

	public String getTextID() {
		return textID;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public boolean isInherited() {
		return inherited;
	}
	public void setInherited(boolean inherited) {
		this.inherited = inherited;
	}

	public double getMutationChance() {
		return mutationChance;
	}
	public void setMutationChance(double mutationChance) {
		this.mutationChance = mutationChance;
	}

	public double getMutationExtent() {
		return mutationExtent;
	}
	public void setMutationExtent(double mutationExtent) {
		this.mutationExtent = mutationExtent;
	}
}
