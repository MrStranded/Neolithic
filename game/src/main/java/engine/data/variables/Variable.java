package engine.data.variables;

import engine.data.IDInterface;
import engine.data.attributes.Attribute;
import engine.data.entities.Instance;
import engine.data.planetary.Tile;
import engine.data.proto.Data;
import engine.utils.converters.StringConverter;

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

	public boolean hasName() {
		return (name != null);
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
			default:
				return "[UNKNOWN VARIABLE TYPE]";
		}
	}
}
