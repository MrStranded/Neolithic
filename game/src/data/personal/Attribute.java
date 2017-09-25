package data.personal;

/**
 * A 'pointer' to the ProtoAttributes in the Data class.
 *
 * Created by Michael on 25.09.2017.
 */
public class Attribute {

	private int id;
	private int value;

	public Attribute(int id,int value) {
		this.id = id;
		this.value = value;
	}

	// ###################################################################################
	// ################################ Getters & Setters ################################
	// ###################################################################################

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}

}
