package engine.data.variables;

import engine.data.IDInterface;
import engine.utils.converters.StringConverter;

public class Variable implements IDInterface {

	private int id;
	private String name;

	private DataType type = DataType.NUMBER;
	private Object value = 0d;

	public Variable(String name) {
		this.name = name;
		id = StringConverter.toID(name);
	}

	public Variable(String name, Variable other) {
		this.name = name;
		this.type = other.type;
		this.value = other.copyValue();
		id = StringConverter.toID(name);
	}

	public Object copyValue() {
		switch (type) {
			case NUMBER:
				return new Double((Double) value);
			case STRING:
				return new String((String) value);
			default:
				return value;
		}
	}

	public boolean isNull() {
		switch (type) {
			case NUMBER:
				if (value == null || (Double) value == 0d) {
					return true;
				}
				return false;
			case STRING:
				if (value == null || ((String) value).length() == 0) {
					return true;
				}
				return false;
			default:
				return (value == null);
		}
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public double getDouble() {
		if (type == DataType.NUMBER) {
			return (Double) value;
		}
		return 0d;
	}
	public void setDouble(double v) {
		type = DataType.NUMBER;
		value = v;
	}

	public String getString() {
		switch (type) {
			case NUMBER:
				return String.valueOf(value);
			case STRING:
				return (String) value;
			default:
				return "[CANNOT CAST TO STRING]";
		}
	}
	public void setString(String v) {
		type = DataType.STRING;
		value = v;
	}

	// ###################################################################################
	// ################################ Getters and Setters (IDInterface) ################
	// ###################################################################################

	@Override
	public int getId() {
		return id;
	}

	@Override
	public IDInterface merge(IDInterface other) {
		return this;
	}

	// ###################################################################################
	// ################################ Debugging ########################################
	// ###################################################################################

	public String toString() {
		String pre = name + " [" + type + "] ";
		switch (type) {
			case NUMBER:
				return pre+ (Double) value;
			case STRING:
				return pre + (String) value;
			default:
				return "[UNKNOWN VARIABLE TYPE]";
		}
	}
}
