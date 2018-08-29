package engine.data.variables;

public class Variable {

	private DataType type;
	private Object value = null;

	public Variable(DataType type) {
		this.type = type;
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

}
