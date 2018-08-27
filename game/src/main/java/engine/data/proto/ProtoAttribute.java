package engine.data.proto;

public class ProtoAttribute {

	private String textID;
	private String name;

	private double mutationChance = 0;

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
}
