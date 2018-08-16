package engine.data.proto;

public class ProtoAttribute {

	private String textID;
	private String name;

	private double mutationChance = 0;

	public ProtoAttribute(String textID, String name) {
		this.textID = textID;
		this.name = name;
	}
}
