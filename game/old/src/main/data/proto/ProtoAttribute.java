package main.data.proto;

/**
 * The Attribute class links to its corresponding ProtoAttribute via the id.
 * The ProtoAttribute holds more info than the light-weight personal version.
 *
 * Created by Michael on 25.09.2017.
 */
public class ProtoAttribute {

	private String name;
	private String textId;
	private int id;
	private boolean flag;
	private boolean mutation;

	public ProtoAttribute(String name, String textId, boolean flag, boolean mutation) {
		this.name = name;
		this.textId = textId;
		this.flag = flag;
		this.mutation = mutation;
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getTextId() {
		return textId;
	}
	public void setTextId(String textId) {
		this.textId = textId;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public boolean canMutate() {
		return mutation;
	}
	public void setMutation(boolean mutation) {
		this.mutation = mutation;
	}
}
