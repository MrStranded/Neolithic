package engine.data.variables;

import engine.data.Data;
import engine.data.IDInterface;
import engine.data.Script;
import engine.data.attributes.Attribute;
import engine.data.entities.Effect;
import engine.data.entities.Instance;
import engine.data.planetary.Tile;
import engine.data.proto.Container;
import engine.data.proto.ProtoAttribute;
import engine.parser.utils.Logger;
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

	public Variable(Container value) {
		this.type = DataType.CONTAINER;
		this.value = value;
	}

	public Variable(Attribute value) {
		this.type = DataType.ATTRIBUTE;
		this.value = value;
	}

	public Variable(Script value) {
		this.type = DataType.SCRIPT;
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
	// ################################ Quick Math #######################################
	// ###################################################################################

	public Variable quickSetAttributeValue(double value) {
		Attribute attribute = getAttribute();
		if (attribute != null) {
			attribute.setValue((int) value);
		}
		return this;
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
			if (value != null) {
				return ((Attribute) value).getValue();
			}
		} else if (type == DataType.LIST) {
			return ((List<Variable>) value).size();
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
		} else if (type == DataType.LIST) {
			return ((List<Variable>) value).size();
		}
		return 0;
	}
	public void setInt(int v) {
		type = DataType.NUMBER;
		value = (double) v;
	}

	public int getContainerId() {
		if (type == DataType.CONTAINER) {
			return Data.getContainerID(((Container) value).getTextID());
		} else if (type == DataType.STRING) {
			return Data.getContainerID((String) value);
		} else if (type == DataType.NUMBER){
			return (int) value;
		} else if (type == DataType.INSTANCE) {
			return ((Instance) value).getId();
		}
		return -1;
	}

	// ----------------------------------------------- string
	public String getString() {
		switch (type) {
			case NUMBER:
				return String.valueOf(value);
			case STRING:
				return (String) value;
			case CONTAINER:
				return ((Container) value).getName();
			case INSTANCE:
				return ((Instance) value).getName();
			case ATTRIBUTE:
				if (value != null) {
					ProtoAttribute protoAttribute = Data.getProtoAttribute(((Attribute) value).getId());
					if (protoAttribute != null) {
						return protoAttribute.getName() + " (" + ((Attribute) value).getValue() + ")";
					}
					return String.valueOf(((Attribute) value).getValue());
				} else {
					return "(NULL ATTRIBUTE)";
				}
			case SCRIPT:
				if (value != null) {
					return ((Script) value).getTextId();
				} else {
					return "(NULL SCRIPT)";
				}
			case LIST:
				StringBuilder values = new StringBuilder("[");
				boolean first = true;
				for (Variable variable : (List<Variable>) value) {
					if (variable != this) {
						if (!first) {
							values.append(", ");
						}
						values.append(variable.getString());
						first = false;
					}
				}
				values.append("]");
				return values.toString();
			default:
				return toString();
				//return "(CANNOT CAST TO STRING: " + toString() + ")";
		}
	}
	public void setString(String v) {
		type = DataType.STRING;
		value = v;
	}

	// ----------------------------------------------- attribute
	public Attribute getAttribute() {
		if (type == DataType.ATTRIBUTE) {
			return (Attribute) value;
		}
		return null;
	}
	public void setAttribute(Attribute v) {
		type = DataType.ATTRIBUTE;
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

	// ----------------------------------------------- container
	public Container getContainer() {
		if (type == DataType.CONTAINER) {
			return (Container) value;
		}
		return null;
	}
	public void setContainer(Container v) {
		type = DataType.CONTAINER;
		value = v;
	}

	// ----------------------------------------------- script
	public Script getScript() {
		if (type == DataType.SCRIPT) {
			return (Script) value;
		}
		return null;
	}
	public void setScript(Script v) {
		type = DataType.SCRIPT;
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

	/**
	 * This method checks whether the linked instance still exists. Otherwise it denotes to remove the reference, so the garbage
	 * collector can do his thing.
	 * This method is called from Instance.getVariable
	 */
	public boolean isInvalid() {
		if (type == DataType.INSTANCE || type == DataType.EFFECT || type == DataType.TILE) {
			if (((Instance) value).isSlatedForRemoval()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public IDInterface merge(IDInterface other) {
		Logger.error("Variable insertion collision! hash from " + toString() + " : " + getId() + " | hash from " + other.toString() + " : " + other.getId() + "");
		return other; // replace old value with new one
	}

	// ###################################################################################
	// ################################ Debugging ########################################
	// ###################################################################################

	public String toString() {
		String pre = (name != null? name : "") + " (" + type + "): ";
		switch (type) {
			case NUMBER:
				return pre+ (Double) value;
			case STRING:
				return pre + (String) value;
			case TILE:
				return pre + (Tile) value;
			case INSTANCE:
				return pre + (Instance) value;
			case CONTAINER:
				return pre + (Container) value;
			case ATTRIBUTE:
				return pre + (Attribute) value;
			case SCRIPT:
				return pre + ((Script) value).getTextId();
			case LIST:
				if (value != null) {
					StringBuilder values = new StringBuilder("[");
					boolean first = true;
					for (Variable variable : (List<Variable>) value) {
						if (variable != this) {
							if (!first) {
								values.append(", ");
							}
							values.append(variable.getString());
							first = false;
						}
					}
					values.append("]");
					return pre + values;
				}
				return pre + "EMPTY";
			default:
				return "UNKNOWN VARIABLE TYPE";
		}
	}
}
