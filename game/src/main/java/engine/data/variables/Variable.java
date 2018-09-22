package engine.data.variables;

import engine.data.Data;
import engine.data.IDInterface;
import engine.data.attributes.Attribute;
import engine.data.entities.Instance;
import engine.data.planetary.Tile;
import engine.utils.converters.StringConverter;

import java.util.List;

public class Variable implements IDInterface {

	private int id;
	private String name = null;

	private DataType type = DataType.NUMBER;
	private Object value = 0d;

	public Variable() {}

	public Variable(String name, Variable other) {
		setName(name);
		copyValue(other);
	}

	/**
	 * Use this static method to create a new, empty variable with the specified name.
	 * The reason this is done so unintuitively is because the constructor Variable(String) is already used to define a new variable with the given string as a value.
	 * @param name of the variable
	 * @return the variable
	 */
	public static Variable withName(String name) {
		Variable variable = new Variable();
		variable.setName(name);
		return variable;
	}

	// ###################################################################################
	// ################################ Specific Constructors ############################
	// ###################################################################################

	public Variable(double value) {
		this.type = DataType.NUMBER;
		this.value = value;
	}

	public Variable(String value) {
		this.type = DataType.STRING;
		this.value = value;
	}

	public Variable(Tile value) {
		this.type = DataType.TILE;
		this.value = value;
	}

	public Variable(Instance value) {
		this.type = DataType.INSTANCE;
		this.value = value;
	}

	public Variable(Attribute value) {
		this.type = DataType.ATTRIBUTE;
		this.value = value;
	}

	public Variable(List<Variable> value) {
		this.type = DataType.LIST;
		this.value = value;
	}

	// ###################################################################################
	// ################################ Value Copying ####################################
	// ###################################################################################

	public void copyValue(Variable other) {
		type = other.type;
		value = other.value;
	}

	// ###################################################################################
	// ################################ Emptiness Check ##################################
	// ###################################################################################

	public boolean isNull() {
		switch (type) {
			case NUMBER:
				return (value == null || (Double) value == 0d);
			case STRING:
				return (value == null || ((String) value).length() == 0);
			case LIST:
				return (value == null || ((List<Variable>) value).isEmpty());
			default:
				return (value == null);
		}
	}

	public boolean hasName() {
		return (name != null);
	}

	// ###################################################################################
	// ################################ Equality Check ###################################
	// ###################################################################################

	public boolean equals(Variable other) {
		switch (type) {
			case NUMBER:
				return ((double) value) == other.getDouble();
			case STRING:
				return ((String) value).equals(other.getString());
			case ATTRIBUTE:
				return getDouble() == other.getDouble();
			case LIST:
				if (other.getType() == DataType.LIST) {
					if (value != null && other.value != null) {
						List<Variable> list1 = (List<Variable>) value;
						List<Variable> list2 = (List<Variable>) other.value;
						if (list1.size() == list2.size()) {
							for (int i=0; i<list1.size(); i++) {
								if (!list1.get(i).equals(list2.get(i))) {
									return false;
								}
								return true;
							}
						}
						return false;
					} else {
						return (value == null && other.value == null);
					}
				}
				return false;
			default:
				return ((type == other.type) && (value == other.value));
		}
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	private void setName(String name) {
		this.name = name;
		id = StringConverter.toID(name);
	}

	public DataType getType() {
		return type;
	}

	// ----------------------------------------------- double
	public double getDouble() {
		if (type == DataType.NUMBER) {
			return (Double) value;
		} else if (type == DataType.ATTRIBUTE) {
			return ((Attribute) value).getValue();
		}
		return 0d;
	}
	public void setDouble(double v) {
		type = DataType.NUMBER;
		value = v;
	}

	// ----------------------------------------------- int
	public int getInt() {
		if (type == DataType.NUMBER) {
			return ((Double) value).intValue();
		} else if (type == DataType.ATTRIBUTE) {
			return ((Attribute) value).getValue();
		}
		return 0;
	}
	public void setInt(int v) {
		type = DataType.NUMBER;
		value = (double) v;
	}

	// ----------------------------------------------- string
	public String getString() {
		switch (type) {
			case NUMBER:
				return String.valueOf(value);
			case STRING:
				return (String) value;
			case INSTANCE:
				return ((Instance) value).toString();
			case ATTRIBUTE:
				return String.valueOf(((Attribute) value).getValue());
			default:
				return "[CANNOT CAST TO STRING]";
		}
	}
	public void setString(String v) {
		type = DataType.STRING;
		value = v;
	}

	// ----------------------------------------------- tile
	public Tile getTile() {
		if (type == DataType.TILE) {
			return (Tile) value;
		} else if (type == DataType.INSTANCE) {
			return ((Instance) value).getPosition();
		}
		return null;
	}
	public void setTile(Tile v) {
		type = DataType.TILE;
		value = v;
	}

	// ----------------------------------------------- instance
	public Instance getInstance() {
		if (type == DataType.INSTANCE) {
			return (Instance) value;
		} else if (type == DataType.TILE) {
			return ((Instance) value);
		}
		return null;
	}
	public void setInstance(Instance v) {
		type = DataType.INSTANCE;
		value = v;
	}

	// ----------------------------------------------- list
	public List<Variable> getList() {
		if (type == DataType.LIST) {
			return (List<Variable>) value;
		}
		return null;
	}
	public void setList(List<Variable> v) {
		type = DataType.LIST;
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
		return other; // replace old value with new one
	}

	// ###################################################################################
	// ################################ Debugging ########################################
	// ###################################################################################

	public String toString() {
		String pre = (name != null? name : "NONAME") + " [" + type + "] ";
		switch (type) {
			case NUMBER:
				return pre+ (Double) value;
			case STRING:
				return pre + (String) value;
			case TILE:
				return pre + (Tile) value;
			case INSTANCE:
				return pre + (Instance) value;
			case ATTRIBUTE:
				return pre + (Attribute) value;
			case LIST:
				if (value != null) {
					StringBuilder values = new StringBuilder();
					boolean first = true;
					for (Variable variable : (List<Variable>) value) {
						if (!first) {
							values.append(", ");
						}
						values.append(variable.toString());
						first = false;
					}
				}
				return pre + "EMPTY";
			default:
				return "[UNKNOWN VARIABLE TYPE]";
		}
	}
}
